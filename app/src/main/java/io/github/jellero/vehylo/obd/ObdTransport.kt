package io.github.jellero.vehylo.obd

interface ObdTransport {
    suspend fun connect()
    suspend fun transact(command: String): String
    suspend fun disconnect()
}
