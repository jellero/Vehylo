package io.github.jellero.vehylo.telemetry

import io.github.jellero.vehylo.mapping.SignalMapping
import io.github.jellero.vehylo.mapping.VehicleFrame

class MappedFrameDecoder {
    fun decode(
        frame: VehicleFrame,
        mappings: List<SignalMapping>,
    ): List<TelemetrySample> = mappings
        .asSequence()
        .filter { it.frameId == frame.id }
        .mapNotNull { mapping ->
            runCatching {
                TelemetrySample(
                    key = TelemetryKey.custom(mapping.key, mapping.label, mapping.unit),
                    value = mapping.decode(frame),
                    timestampNanos = frame.timestampNanos,
                    source = TelemetrySourceKind.CUSTOM_MAPPING,
                )
            }.getOrNull()
        }
        .toList()
}
