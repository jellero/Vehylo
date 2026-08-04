package io.github.jellero.vehylo.obd

import io.github.jellero.vehylo.telemetry.TelemetrySample
import io.github.jellero.vehylo.telemetry.TelemetrySource
import io.github.jellero.vehylo.telemetry.TelemetrySourceKind
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObdTelemetrySource(
    private val client: Elm327Client,
    private val requestedPids: List<ObdPid> = listOf(
        ObdPid.EngineRpm,
        ObdPid.VehicleSpeed,
        ObdPid.CoolantTemperature,
        ObdPid.EngineOilTemperature,
        ObdPid.IntakeAirTemperature,
        ObdPid.AmbientAirTemperature,
        ObdPid.EngineLoad,
        ObdPid.ThrottlePosition,
        ObdPid.IntakeManifoldPressure,
        ObdPid.FuelPressure,
        ObdPid.FuelLevel,
        ObdPid.ControlModuleVoltage,
    ),
) : TelemetrySource {
    private var running = false
    private var activePids: List<ObdPid> = requestedPids

    override suspend fun connect() {
        client.connect()
        val supported = runCatching { client.supportedMode01Pids() }.getOrDefault(emptySet())
        activePids = requestedPids.filter { it.pid in supported }.ifEmpty { requestedPids }
        running = true
    }

    override suspend fun disconnect() {
        running = false
        client.disconnect()
    }

    override fun samples(): Flow<TelemetrySample> = flow {
        while (running) {
            activePids.forEach { pid ->
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
            delay(250)
        }
    }
}
