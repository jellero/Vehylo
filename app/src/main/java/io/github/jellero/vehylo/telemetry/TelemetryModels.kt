package io.github.jellero.vehylo.telemetry

import kotlin.time.Duration

enum class TelemetryKey(val label: String, val unit: String) {
    ENGINE_RPM("Regime motore", "rpm"),
    VEHICLE_SPEED("Velocità", "km/h"),
    COOLANT_TEMPERATURE("Liquido motore", "°C"),
    THROTTLE_POSITION("Acceleratore", "%"),
    CONTROL_MODULE_VOLTAGE("Tensione ECU", "V"),
    ROLL("Rollio", "°"),
    PITCH("Beccheggio", "°")
}

enum class TelemetrySourceKind {
    DEMO,
    OBD,
    BLE_INCLINOMETER
}

data class TelemetrySample(
    val key: TelemetryKey,
    val value: Double,
    val timestampNanos: Long = System.nanoTime(),
    val source: TelemetrySourceKind,
)

interface TelemetrySource {
    suspend fun connect()
    suspend fun disconnect()
    fun samples(): kotlinx.coroutines.flow.Flow<TelemetrySample>
}

data class PollingRate(
    val fast: Duration,
    val normal: Duration,
    val slow: Duration,
)
