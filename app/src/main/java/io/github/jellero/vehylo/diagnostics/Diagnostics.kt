package io.github.jellero.vehylo.diagnostics

import io.github.jellero.vehylo.obd.ObdTransport

enum class DtcStatus {
    STORED,
    PENDING,
    PERMANENT,
}

data class DiagnosticTroubleCode(
    val code: String,
    val status: DtcStatus,
)

data class MonitorStatus(
    val malfunctionIndicatorLampOn: Boolean,
    val confirmedDtcCount: Int,
)

data class DiagnosticSnapshot(
    val monitorStatus: MonitorStatus?,
    val storedCodes: List<DiagnosticTroubleCode>,
    val pendingCodes: List<DiagnosticTroubleCode>,
    val permanentCodes: List<DiagnosticTroubleCode>,
)

class ReadOnlyDiagnosticService(
    private val transport: ObdTransport,
) {
    suspend fun readSnapshot(): DiagnosticSnapshot = DiagnosticSnapshot(
        monitorStatus = readMonitorStatus(),
        storedCodes = readCodes("03", 0x43, DtcStatus.STORED),
        pendingCodes = readCodes("07", 0x47, DtcStatus.PENDING),
        permanentCodes = readCodes("0A", 0x4A, DtcStatus.PERMANENT),
    )

    suspend fun readMonitorStatus(): MonitorStatus? {
        val bytes = ElmHexResponse.parse(transport.transact("0101"), echoedCommand = "0101")
        val responseIndex = bytes.indexOfSequence(byteArrayOf(0x41, 0x01))
        if (responseIndex < 0 || responseIndex + 2 >= bytes.size) return null
        val status = bytes[responseIndex + 2].toInt() and 0xFF
        return MonitorStatus(
            malfunctionIndicatorLampOn = status and 0x80 != 0,
            confirmedDtcCount = status and 0x7F,
        )
    }

    private suspend fun readCodes(
        command: String,
        responseService: Int,
        status: DtcStatus,
    ): List<DiagnosticTroubleCode> {
        val bytes = ElmHexResponse.parse(transport.transact(command), echoedCommand = command)
        val start = bytes.indexOf(responseService.toByte())
        if (start < 0) return emptyList()
        return DtcParser.parse(bytes.copyOfRange(start + 1, bytes.size), status)
    }
}

object DtcParser {
    fun parse(payload: ByteArray, status: DtcStatus): List<DiagnosticTroubleCode> =
        payload.asList()
            .chunked(2)
            .mapNotNull { pair ->
                if (pair.size < 2) return@mapNotNull null
                val first = pair[0].toInt() and 0xFF
                val second = pair[1].toInt() and 0xFF
                if (first == 0 && second == 0) return@mapNotNull null
                DiagnosticTroubleCode(decode(first, second), status)
            }

    private fun decode(first: Int, second: Int): String {
        val family = when ((first ushr 6) and 0x03) {
            0 -> 'P'
            1 -> 'C'
            2 -> 'B'
            else -> 'U'
        }
        val digit1 = (first ushr 4) and 0x03
        val digit2 = first and 0x0F
        val digit3 = (second ushr 4) and 0x0F
        val digit4 = second and 0x0F
        return "$family$digit1${digit2.toString(16)}${digit3.toString(16)}${digit4.toString(16)}"
            .uppercase()
    }
}

object ElmHexResponse {
    fun parse(response: String, echoedCommand: String? = null): ByteArray {
        if (response.contains("NO DATA", ignoreCase = true)) return byteArrayOf()
        val echo = echoedCommand?.replace(" ", "")?.uppercase()
        return response.lineSequence()
            .map { it.substringBefore('>').trim().replace(" ", "").uppercase() }
            .filter { it.isNotBlank() }
            .filterNot { it == echo }
            .filter { it.length % 2 == 0 && it.matches(Regex("[0-9A-F]+")) }
            .flatMap { line -> line.chunked(2).asSequence() }
            .map { it.toInt(16).toByte() }
            .toList()
            .toByteArray()
    }
}

private fun ByteArray.indexOfSequence(sequence: ByteArray): Int {
    if (sequence.isEmpty() || sequence.size > size) return -1
    for (start in 0..(size - sequence.size)) {
        if (sequence.indices.all { index -> this[start + index] == sequence[index] }) return start
    }
    return -1
}
