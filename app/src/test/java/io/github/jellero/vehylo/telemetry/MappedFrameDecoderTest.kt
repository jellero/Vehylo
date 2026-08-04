package io.github.jellero.vehylo.telemetry

import io.github.jellero.vehylo.mapping.ByteOrder
import io.github.jellero.vehylo.mapping.MappingOrigin
import io.github.jellero.vehylo.mapping.SignalMapping
import io.github.jellero.vehylo.mapping.VehicleFrame
import org.junit.Assert.assertEquals
import org.junit.Test

class MappedFrameDecoderTest {
    @Test
    fun createsDynamicTelemetryMetric() {
        val mapping = SignalMapping(
            key = "transmission.oil_temperature",
            label = "Olio cambio",
            frameId = 0x321,
            startBit = 0,
            bitLength = 8,
            byteOrder = ByteOrder.BIG_ENDIAN,
            signed = false,
            scale = 1.0,
            offset = -40.0,
            unit = "°C",
            origin = MappingOrigin.WIZARD,
        )

        val result = MappedFrameDecoder().decode(
            frame = VehicleFrame(0x321, byteArrayOf(120)),
            mappings = listOf(mapping),
        ).single()

        assertEquals("transmission.oil_temperature", result.key.id)
        assertEquals(80.0, result.value, 0.001)
        assertEquals(TelemetrySourceKind.CUSTOM_MAPPING, result.source)
    }
}
