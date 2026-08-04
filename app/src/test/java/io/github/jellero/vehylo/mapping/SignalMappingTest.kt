package io.github.jellero.vehylo.mapping

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalMappingTest {
    @Test
    fun extractsBigAndLittleEndianValues() {
        val data = byteArrayOf(0x12, 0x34)
        assertEquals(
            0x1234L,
            BitSignalCodec.extract(data, 0, 16, ByteOrder.BIG_ENDIAN, signed = false),
        )
        assertEquals(
            0x3412L,
            BitSignalCodec.extract(data, 0, 16, ByteOrder.LITTLE_ENDIAN, signed = false),
        )
    }

    @Test
    fun signExtendsMappedValues() {
        assertEquals(
            -2L,
            BitSignalCodec.extract(byteArrayOf(0xFE.toByte()), 0, 8, ByteOrder.BIG_ENDIAN, signed = true),
        )
    }

    @Test
    fun inferenceFindsCorrelatedByte() {
        val observations = (0..20).map { index ->
            val raw = index * 5
            ReferenceObservation(
                frame = VehicleFrame(0x321, byteArrayOf(0, 0, raw.toByte(), 0, 0, 0, 0, 0)),
                referenceValue = raw * 0.5 + 10.0,
            )
        }

        val candidates = MappingInferenceEngine().inferFromReference(
            signalKey = "speed",
            signalLabel = "Velocità",
            unit = "km/h",
            observations = observations,
        )

        val expected = candidates.firstOrNull {
            it.mapping.startBit == 16 && it.mapping.bitLength == 8 && !it.mapping.signed
        }
        assertTrue(expected != null)
        assertEquals(0.5, expected!!.mapping.scale, 1e-9)
        assertEquals(10.0, expected.mapping.offset, 1e-9)
        assertTrue(expected.confidence > 0.99)
    }
}
