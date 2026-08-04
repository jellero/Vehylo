package io.github.jellero.vehylo.obd

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdCapabilityScannerTest {
    @Test
    fun discoversSupportedMode01Pids() = runBlocking {
        val transport = object : ObdTransport {
            override suspend fun connect() = Unit
            override suspend fun disconnect() = Unit
            override suspend fun transact(command: String): String = when (command) {
                "0100" -> "41 00 18 1B 80 01"
                "0120" -> "41 20 00 00 00 00"
                else -> "NO DATA"
            }
        }

        val supported = ObdCapabilityScanner(transport).scanMode01()
        assertTrue(0x04 in supported)
        assertTrue(0x05 in supported)
        assertTrue(0x0C in supported)
        assertTrue(0x0D in supported)
        assertTrue(0x20 in supported)
    }
}
