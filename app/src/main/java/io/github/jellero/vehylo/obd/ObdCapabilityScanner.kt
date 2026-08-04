package io.github.jellero.vehylo.obd

class ObdCapabilityScanner(
    private val transport: ObdTransport,
) {
    suspend fun scanMode01(): Set<Int> {
        val supported = linkedSetOf<Int>()
        var basePid = 0x00

        while (basePid <= 0xC0) {
            val mask = try {
                val command = "01%02X".format(basePid)
                parseMask(transport.transact(command), basePid)
            } catch (_: Throwable) {
                break
            }

            repeat(32) { bitIndex ->
                val maskBit = 1L shl (31 - bitIndex)
                if (mask and maskBit != 0L) supported += basePid + bitIndex + 1
            }

            val nextBase = basePid + 0x20
            if (nextBase > 0xC0 || nextBase !in supported) break
            basePid = nextBase
        }
        return supported
    }

    private fun parseMask(rawResponse: String, basePid: Int): Long {
        val expectedHeader = "41%02X".format(basePid)
        val responseLine = rawResponse
            .uppercase()
            .replace("SEARCHING...", "")
            .lineSequence()
            .map { line -> line.replace(Regex("[^0-9A-F]"), "") }
            .firstOrNull { line -> line.contains(expectedHeader) }
            ?: error("Risposta capability OBD non valida per PID base 0x${basePid.toString(16)}")

        val payload = responseLine.substringAfter(expectedHeader)
        require(payload.length >= 8) { "Maschera capability OBD incompleta" }
        return payload.take(8).toLong(16)
    }
}
