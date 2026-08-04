package io.github.jellero.vehylo.bluetooth

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.assertEquals
import org.junit.Test

class InclinationPayloadParserTest {
    @Test
    fun parsesTwoLittleEndianFloatValues() {
        val payload = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(12.5f)
            .putFloat(-3.25f)
            .array()

        val result = InclinationPayloadParser.parseFloat32LittleEndian(payload)

        assertEquals(12.5, result.rollDegrees, 0.001)
        assertEquals(-3.25, result.pitchDegrees, 0.001)
    }
}
