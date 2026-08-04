package io.github.jellero.vehylo.mapping

enum class MappingWizardStep {
    SIGNAL,
    FRAME,
    LAYOUT,
    CALIBRATION,
    REVIEW,
    COMPLETE,
}

data class CalibrationPoint(
    val rawValue: Double,
    val physicalValue: Double,
)

data class MappingWizardState(
    val step: MappingWizardStep = MappingWizardStep.SIGNAL,
    val key: String = "",
    val label: String = "",
    val unit: String = "",
    val frameId: Long? = null,
    val startBit: Int = 0,
    val bitLength: Int = 8,
    val byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
    val signed: Boolean = false,
    val scale: Double = 1.0,
    val offset: Double = 0.0,
    val calibrationPoints: List<CalibrationPoint> = emptyList(),
)

class MappingWizardSession(
    initialState: MappingWizardState = MappingWizardState(),
) {
    var state: MappingWizardState = initialState
        private set

    fun defineSignal(key: String, label: String, unit: String) {
        require(key.isNotBlank())
        require(label.isNotBlank())
        state = state.copy(
            step = MappingWizardStep.FRAME,
            key = key.trim(),
            label = label.trim(),
            unit = unit.trim(),
        )
    }

    fun selectFrame(frameId: Long) {
        require(frameId >= 0)
        state = state.copy(step = MappingWizardStep.LAYOUT, frameId = frameId)
    }

    fun defineLayout(
        startBit: Int,
        bitLength: Int,
        byteOrder: ByteOrder,
        signed: Boolean,
    ) {
        require(startBit >= 0)
        require(bitLength in 1..63)
        state = state.copy(
            step = MappingWizardStep.CALIBRATION,
            startBit = startBit,
            bitLength = bitLength,
            byteOrder = byteOrder,
            signed = signed,
        )
    }

    fun addCalibrationPoint(rawValue: Double, physicalValue: Double) {
        require(rawValue.isFinite() && physicalValue.isFinite())
        state = state.copy(
            calibrationPoints = state.calibrationPoints + CalibrationPoint(rawValue, physicalValue)
        )
    }

    fun calculateCalibration() {
        val points = state.calibrationPoints
        require(points.size >= 2) { "Servono almeno due punti di calibrazione" }
        val meanRaw = points.map { it.rawValue }.average()
        val meanPhysical = points.map { it.physicalValue }.average()
        val variance = points.sumOf { (it.rawValue - meanRaw) * (it.rawValue - meanRaw) }
        require(variance > 1e-12) { "I valori grezzi di calibrazione devono essere differenti" }
        val covariance = points.sumOf {
            (it.rawValue - meanRaw) * (it.physicalValue - meanPhysical)
        }
        val scale = covariance / variance
        val offset = meanPhysical - scale * meanRaw
        state = state.copy(
            step = MappingWizardStep.REVIEW,
            scale = scale,
            offset = offset,
        )
    }

    fun useManualCalibration(scale: Double, offset: Double) {
        require(scale.isFinite() && offset.isFinite())
        state = state.copy(
            step = MappingWizardStep.REVIEW,
            scale = scale,
            offset = offset,
        )
    }

    fun acceptCandidate(candidate: MappingCandidate) {
        val mapping = candidate.mapping
        state = MappingWizardState(
            step = MappingWizardStep.REVIEW,
            key = mapping.key,
            label = mapping.label,
            unit = mapping.unit,
            frameId = mapping.frameId,
            startBit = mapping.startBit,
            bitLength = mapping.bitLength,
            byteOrder = mapping.byteOrder,
            signed = mapping.signed,
            scale = mapping.scale,
            offset = mapping.offset,
        )
    }

    fun complete(): SignalMapping {
        val frameId = requireNotNull(state.frameId) { "Frame non selezionato" }
        val mapping = SignalMapping(
            key = state.key,
            label = state.label,
            frameId = frameId,
            startBit = state.startBit,
            bitLength = state.bitLength,
            byteOrder = state.byteOrder,
            signed = state.signed,
            scale = state.scale,
            offset = state.offset,
            unit = state.unit,
            origin = MappingOrigin.WIZARD,
        )
        state = state.copy(step = MappingWizardStep.COMPLETE)
        return mapping
    }

    fun reset() {
        state = MappingWizardState()
    }
}
