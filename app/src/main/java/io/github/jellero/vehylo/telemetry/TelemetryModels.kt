package io.github.jellero.vehylo.telemetry

import kotlin.time.Duration

class TelemetryKey private constructor(
    val id: String,
    val label: String,
    val unit: String,
) {
    init {
        require(id.isNotBlank()) { "L'identificativo del parametro è obbligatorio" }
        require(label.isNotBlank()) { "L'etichetta del parametro è obbligatoria" }
    }

    override fun equals(other: Any?): Boolean = other is TelemetryKey && id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = id

    companion object {
        val ENGINE_RPM = TelemetryKey("engine.rpm", "Regime motore", "rpm")
        val VEHICLE_SPEED = TelemetryKey("vehicle.speed", "Velocità", "km/h")
        val COOLANT_TEMPERATURE = TelemetryKey("engine.coolant_temperature", "Liquido motore", "°C")
        val ENGINE_OIL_TEMPERATURE = TelemetryKey("engine.oil_temperature", "Olio motore", "°C")
        val TRANSMISSION_OIL_TEMPERATURE = TelemetryKey("transmission.oil_temperature", "Olio cambio", "°C")
        val INTAKE_AIR_TEMPERATURE = TelemetryKey("engine.intake_air_temperature", "Aria aspirata", "°C")
        val AMBIENT_AIR_TEMPERATURE = TelemetryKey("vehicle.ambient_air_temperature", "Temperatura esterna", "°C")
        val ENGINE_LOAD = TelemetryKey("engine.load", "Carico motore", "%")
        val THROTTLE_POSITION = TelemetryKey("engine.throttle_position", "Acceleratore", "%")
        val FUEL_LEVEL = TelemetryKey("fuel.level", "Livello carburante", "%")
        val FUEL_PRESSURE = TelemetryKey("fuel.pressure", "Pressione carburante", "kPa")
        val INTAKE_MANIFOLD_PRESSURE = TelemetryKey("engine.intake_manifold_pressure", "Pressione collettore", "kPa")
        val BOOST_PRESSURE = TelemetryKey("engine.boost_pressure", "Pressione turbo", "kPa")
        val EXHAUST_GAS_TEMPERATURE = TelemetryKey("engine.exhaust_gas_temperature", "Temperatura gas di scarico", "°C")
        val DPF_TEMPERATURE = TelemetryKey("engine.dpf_temperature", "Temperatura DPF", "°C")
        val BATTERY_TEMPERATURE = TelemetryKey("battery.temperature", "Temperatura batteria", "°C")
        val CONTROL_MODULE_VOLTAGE = TelemetryKey("electrical.control_module_voltage", "Tensione ECU", "V")
        val STEERING_ANGLE = TelemetryKey("chassis.steering_angle", "Angolo sterzo", "°")
        val GEAR_POSITION = TelemetryKey("transmission.gear_position", "Marcia", "")
        val ROLL = TelemetryKey("attitude.roll", "Rollio", "°")
        val PITCH = TelemetryKey("attitude.pitch", "Beccheggio", "°")

        val BUILT_INS: List<TelemetryKey> = listOf(
            ENGINE_RPM,
            VEHICLE_SPEED,
            COOLANT_TEMPERATURE,
            ENGINE_OIL_TEMPERATURE,
            TRANSMISSION_OIL_TEMPERATURE,
            INTAKE_AIR_TEMPERATURE,
            AMBIENT_AIR_TEMPERATURE,
            ENGINE_LOAD,
            THROTTLE_POSITION,
            FUEL_LEVEL,
            FUEL_PRESSURE,
            INTAKE_MANIFOLD_PRESSURE,
            BOOST_PRESSURE,
            EXHAUST_GAS_TEMPERATURE,
            DPF_TEMPERATURE,
            BATTERY_TEMPERATURE,
            CONTROL_MODULE_VOLTAGE,
            STEERING_ANGLE,
            GEAR_POSITION,
            ROLL,
            PITCH,
        )

        fun custom(id: String, label: String, unit: String): TelemetryKey =
            TelemetryKey(id = id.trim(), label = label.trim(), unit = unit.trim())
    }
}

enum class TelemetrySourceKind {
    DEMO,
    OBD,
    BLE_INCLINOMETER,
    CUSTOM_MAPPING,
    GPS,
    VEHICLE_API,
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
