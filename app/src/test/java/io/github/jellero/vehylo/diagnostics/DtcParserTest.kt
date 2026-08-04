package io.github.jellero.vehylo.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Test

class DtcParserTest {
    @Test
    fun decodesGenericDiagnosticCodes() {
        val codes = DtcParser.parse(
            byteArrayOf(0x01, 0x33, 0xC1.toByte(), 0x00, 0x00, 0x00),
            DtcStatus.STORED,
        )

        assertEquals(listOf("P0133", "U0100"), codes.map { it.code })
    }

    @Test
    fun parsesElmResponseWithoutSpaces() {
        val bytes = ElmHexResponse.parse("03\r4301330000\r>", echoedCommand = "03")
        assertEquals(listOf(0x43, 0x01, 0x33, 0x00, 0x00), bytes.map { it.toInt() and 0xFF })
    }
}
