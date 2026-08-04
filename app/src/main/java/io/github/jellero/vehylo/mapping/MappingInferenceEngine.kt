package io.github.jellero.vehylo.mapping

import kotlin.math.abs
import kotlin.math.sqrt

data class ReferenceObservation(
    val frame: VehicleFrame,
    val referenceValue: Double,
)

data class MappingCandidate(
    val mapping: SignalMapping,
    val correlation: Double,
    val normalizedError: Double,
    val confidence: Double,
    val explanation: String,
)

data class ChangingSignalCandidate(
    val frameId: Long,
    val startBit: Int,
    val bitLength: Int,
    val byteOrder: ByteOrder,
    val activityScore: Double,
)

class MappingInferenceEngine(
    private val candidateBitLengths: List<Int> = listOf(8, 16, 32),
) {
    fun inferFromReference(
        signalKey: String,
        signalLabel: String,
        unit: String,
        observations: List<ReferenceObservation>,
        maxCandidates: Int = 12,
    ): List<MappingCandidate> {
        require(observations.size >= 4) { "Servono almeno quattro osservazioni" }
        require(observations.map { it.frame.id }.distinct().size == 1) {
            "Le osservazioni devono riferirsi allo stesso frame"
        }
        require(observations.map { it.frame.data.size }.distinct().size == 1) {
            "I frame devono avere la stessa lunghezza"
        }

        val frameId = observations.first().frame.id
        val frameBits = observations.first().frame.data.size * 8
        val reference = observations.map { it.referenceValue }
        val referenceRange = (reference.maxOrNull()!! - reference.minOrNull()!!).coerceAtLeast(1e-9)

        return buildList {
            for (bitLength in candidateBitLengths) {
                if (bitLength > frameBits) continue
                for (startBit in 0..(frameBits - bitLength) step 8) {
                    for (byteOrder in ByteOrder.entries) {
                        for (signed in listOf(false, true)) {
                            val rawValues = observations.map { observation ->
                                BitSignalCodec.extract(
                                    data = observation.frame.data,
                                    startBit = startBit,
                                    bitLength = bitLength,
                                    byteOrder = byteOrder,
                                    signed = signed,
                                ).toDouble()
                            }
                            val regression = linearRegression(rawValues, reference) ?: continue
                            val normalizedError = regression.rmse / referenceRange
                            val confidence = (
                                abs(regression.correlation) * (1.0 - normalizedError.coerceIn(0.0, 1.0))
                            ).coerceIn(0.0, 1.0)
                            if (confidence < 0.35) continue

                            val mapping = SignalMapping(
                                key = signalKey,
                                label = signalLabel,
                                frameId = frameId,
                                startBit = startBit,
                                bitLength = bitLength,
                                byteOrder = byteOrder,
                                signed = signed,
                                scale = regression.slope,
                                offset = regression.intercept,
                                unit = unit,
                                origin = MappingOrigin.LEARNED,
                                confidence = confidence,
                            )
                            add(
                                MappingCandidate(
                                    mapping = mapping,
                                    correlation = regression.correlation,
                                    normalizedError = normalizedError,
                                    confidence = confidence,
                                    explanation = "Correlazione ${"%.3f".format(regression.correlation)}, " +
                                        "errore normalizzato ${"%.3f".format(normalizedError)}",
                                )
                            )
                        }
                    }
                }
            }
        }.sortedByDescending { it.confidence }
            .distinctBy {
                listOf(
                    it.mapping.startBit,
                    it.mapping.bitLength,
                    it.mapping.byteOrder,
                    it.mapping.signed,
                )
            }
            .take(maxCandidates)
    }

    fun discoverChangingSignals(
        frames: List<VehicleFrame>,
        maxCandidates: Int = 20,
    ): List<ChangingSignalCandidate> {
        require(frames.size >= 4) { "Servono almeno quattro frame" }
        return frames.groupBy { it.id }.flatMap { (frameId, groupedFrames) ->
            val frameBits = groupedFrames.minOf { it.data.size } * 8
            buildList {
                for (bitLength in candidateBitLengths) {
                    if (bitLength > frameBits) continue
                    for (startBit in 0..(frameBits - bitLength) step 8) {
                        for (byteOrder in ByteOrder.entries) {
                            val values = groupedFrames.map { frame ->
                                BitSignalCodec.extract(
                                    data = frame.data,
                                    startBit = startBit,
                                    bitLength = bitLength,
                                    byteOrder = byteOrder,
                                    signed = false,
                                ).toDouble()
                            }
                            val min = values.minOrNull() ?: continue
                            val max = values.maxOrNull() ?: continue
                            val range = max - min
                            if (range <= 0.0) continue
                            val mean = values.average()
                            val variance = values.sumOf { (it - mean) * (it - mean) } / values.size
                            val activity = (sqrt(variance) / range).coerceIn(0.0, 1.0)
                            add(
                                ChangingSignalCandidate(
                                    frameId = frameId,
                                    startBit = startBit,
                                    bitLength = bitLength,
                                    byteOrder = byteOrder,
                                    activityScore = activity,
                                )
                            )
                        }
                    }
                }
            }
        }.sortedByDescending { it.activityScore }
            .distinctBy { listOf(it.frameId, it.startBit, it.bitLength, it.byteOrder) }
            .take(maxCandidates)
    }

    private fun linearRegression(x: List<Double>, y: List<Double>): Regression? {
        if (x.size != y.size || x.size < 2) return null
        val meanX = x.average()
        val meanY = y.average()
        val varianceX = x.sumOf { (it - meanX) * (it - meanX) }
        val varianceY = y.sumOf { (it - meanY) * (it - meanY) }
        if (varianceX <= 1e-12 || varianceY <= 1e-12) return null

        val covariance = x.indices.sumOf { index ->
            (x[index] - meanX) * (y[index] - meanY)
        }
        val slope = covariance / varianceX
        val intercept = meanY - slope * meanX
        val correlation = covariance / sqrt(varianceX * varianceY)
        val rmse = sqrt(
            x.indices.sumOf { index ->
                val error = y[index] - (x[index] * slope + intercept)
                error * error
            } / x.size
        )
        return Regression(slope, intercept, correlation, rmse)
    }

    private data class Regression(
        val slope: Double,
        val intercept: Double,
        val correlation: Double,
        val rmse: Double,
    )
}
