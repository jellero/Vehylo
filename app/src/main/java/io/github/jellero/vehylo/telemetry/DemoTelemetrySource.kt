package io.github.jellero.vehylo.telemetry

import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoTelemetrySource : TelemetrySource {
    private var running = false

    override suspend fun connect() {
        running = true
    }

    override suspend fun disconnect() {
        running = false
    }

    override fun samples(): Flow<TelemetrySample> = flow {
        var tick = 0L
        while (running) {
            val phase = tick / 18.0
            emitSample(TelemetryKey.ENGINE_RPM, 1_650.0 + sin(phase) * 850.0)
            emitSample(TelemetryKey.VEHICLE_SPEED, 48.0 + sin(phase / 2.0) * 18.0)
            emitSample(TelemetryKey.COOLANT_TEMPERATURE, 88.0 + sin(phase / 5.0) * 3.0)
            emitSample(TelemetryKey.ENGINE_OIL_TEMPERATURE, 96.0 + sin(phase / 6.0) * 4.0)
            emitSample(TelemetryKey.TRANSMISSION_OIL_TEMPERATURE, 78.0 + sin(phase / 7.0) * 5.0)
            emitSample(TelemetryKey.ENGINE_LOAD, 42.0 + sin(phase) * 22.0)
            emitSample(TelemetryKey.THROTTLE_POSITION, 27.0 + sin(phase) * 17.0)
            emitSample(TelemetryKey.INTAKE_MANIFOLD_PRESSURE, 92.0 + sin(phase) * 28.0)
            emitSample(TelemetryKey.FUEL_LEVEL, 67.0)
            emitSample(TelemetryKey.CONTROL_MODULE_VOLTAGE, 14.1 + sin(phase / 7.0) * 0.2)
            emitSample(TelemetryKey.ROLL, sin(phase / 2.2) * 12.0)
            emitSample(TelemetryKey.PITCH, sin(phase / 3.1) * 8.0)
            tick++
            delay(250)
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<TelemetrySample>.emitSample(
        key: TelemetryKey,
        value: Double,
    ) {
        emit(
            TelemetrySample(
                key = key,
                value = value,
                source = TelemetrySourceKind.DEMO,
            )
        )
    }
}
