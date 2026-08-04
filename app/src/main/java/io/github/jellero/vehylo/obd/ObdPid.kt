package io.github.jellero.vehylo.obd

import io.github.jellero.vehylo.telemetry.TelemetryKey

sealed class ObdPid(
    val command: String,
    val key: TelemetryKey,
) {
    data object EngineRpm : ObdPid("010C", TelemetryKey.ENGINE_RPM)
    data object VehicleSpeed : ObdPid("010D", TelemetryKey.VEHICLE_SPEED)
    data object CoolantTemperature : ObdPid("0105", TelemetryKey.COOLANT_TEMPERATURE)
    data object ThrottlePosition : ObdPid("0111", TelemetryKey.THROTTLE_POSITION)
    data object ControlModuleVoltage : ObdPid("0142", TelemetryKey.CONTROL_MODULE_VOLTAGE)
}

object GenericObdParser {
    fun parse(pid: ObdPid, rawResponse: String): Double {
        val data = extractPayload(rawResponse, pid.command)
        return when (pid) {
            ObdPid.EngineRpm -> requireBytes(data, 2).let { (it[0] * 256 + it[1]) / 4.0 }
            ObdPid.VehicleSpeed -> requireBytes(data, 1)[0].toDouble()
            ObdPid.CoolantTemperature -> requireBytes(data, 1)[0] - 40.0
            ObdPid.ThrottlePosition -> requireBytes(data, 1)[0] * 100.0 / 255.0
            ObdPid.ControlModuleVoltage -> requireBytes(data, 2).let { (it[0] * 256 + it[1]) / 1000.0 }
        }
    }

    private fun extractPayload(rawResponse: String, command: String): List<Int> {
        val expectedHeader = "%02X%s".format(
            command.substring(0, 2).toInt(16) + 0x40,
            command.substring(2, 4),
        )

        val responseLine = rawResponse
            .uppercase()
            .replace("SEARCHING...", "")
            .lineSequence()
            .map { line -> line.replace(Regex("[^0-9A-F]"), "") }
            .firstOrNull { line -> line.contains(expectedHeader) }
            ?: error("Risposta OBD non valida per $command: $rawResponse")

        val payloadHex = responseLine.substringAfter(expectedHeader)
        require(payloadHex.length >= 2 && payloadHex.length % 2 == 0) {
            "Payload OBD non valido: $payloadHex"
        }
        return payloadHex.chunked(2).map { it.toInt(16) }
    }

    private fun requireBytes(data: List<Int>, count: Int): List<Int> {
        require(data.size >= count) { "Payload OBD incompleto: attesi $count byte, ricevuti ${data.size}" }
        return data
    }
}
