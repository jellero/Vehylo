package io.github.jellero.vehylo.obd

import io.github.jellero.vehylo.telemetry.TelemetrySample
import io.github.jellero.vehylo.telemetry.TelemetrySource
import io.github.jellero.vehylo.telemetry.TelemetrySourceKind
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObdTelemetrySource(
    private val client: Elm327Client,
    private val pids: List<ObdPid> = listOf(
        ObdPid.EngineRpm,
        ObdPid.VehicleSpeed,
        ObdPid.CoolantTemperature,
        ObdPid.ThrottlePosition,
        ObdPid.ControlModuleVoltage,
    ),
) : TelemetrySource {
    private var running = false

    override suspend fun connect() {
        client.connect()
        running = true
    }

    override suspend fun disconnect() {
        running = false
        client.disconnect()
    }

    override fun samples(): Flow<TelemetrySample> = flow {
        while (running) {
            pids.forEach { pid ->
                runCatching { client.read(pid) }
                    .onSuccess { value ->
                        emit(
                            TelemetrySample(
                                key = pid.key,
                                value = value,
                                source = TelemetrySourceKind.OBD,
                            )
                        )
                    }
            }
            delay(100)
        }
    }
}
