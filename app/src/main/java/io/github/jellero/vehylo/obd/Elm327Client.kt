package io.github.jellero.vehylo.obd

class Elm327Client(
    private val transport: ObdTransport,
) {
    suspend fun connect() {
        transport.connect()
        listOf("ATZ", "ATE0", "ATL0", "ATS0", "ATH0", "ATSP0").forEach { command ->
            transport.transact(command)
        }
    }

    suspend fun read(pid: ObdPid): Double {
        val response = transport.transact(pid.command)
        return GenericObdParser.parse(pid, response)
    }

    suspend fun supportedMode01Pids(): Set<Int> =
        ObdCapabilityScanner(transport).scanMode01()

    suspend fun disconnect() {
        transport.disconnect()
    }
}
