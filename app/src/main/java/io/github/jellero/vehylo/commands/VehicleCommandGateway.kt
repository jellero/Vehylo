package io.github.jellero.vehylo.commands

import io.github.jellero.vehylo.obd.ObdTransport
import java.time.Instant
import java.util.UUID

enum class CommandRisk {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
}

sealed interface VehicleCommand {
    val serviceId: Int
    val payload: ByteArray
    val risk: CommandRisk
    val description: String

    data object ClearDiagnosticInformation : VehicleCommand {
        override val serviceId: Int = 0x04
        override val payload: ByteArray = byteArrayOf()
        override val risk: CommandRisk = CommandRisk.HIGH
        override val description: String = "Cancella i codici diagnostici e i relativi dati salvati"
    }

    data class VendorCommand(
        override val serviceId: Int,
        override val payload: ByteArray,
        override val risk: CommandRisk,
        override val description: String,
    ) : VehicleCommand {
        init {
            require(serviceId in 0x00..0xFF)
            require(description.isNotBlank())
        }
    }
}

data class VehicleState(
    val speedKph: Double?,
    val ignitionOn: Boolean?,
)

data class CommandPolicy(
    val writeEnabled: Boolean = false,
    val requireStationaryVehicle: Boolean = true,
    val allowedServiceIds: Set<Int> = emptySet(),
    val maximumRisk: CommandRisk = CommandRisk.LOW,
)

data class ExecutionPermit internal constructor(
    internal val id: UUID,
    internal val commandFingerprint: String,
    internal val expiresAt: Instant,
)

sealed interface CommandResult {
    data class Blocked(val reason: String) : CommandResult
    data class Executed(val rawResponse: String) : CommandResult
}

class VehicleCommandGateway(
    private val transport: ObdTransport,
    private val policy: CommandPolicy = CommandPolicy(),
    private val now: () -> Instant = Instant::now,
) {
    fun preview(command: VehicleCommand): String = buildCommand(command)

    fun arm(
        command: VehicleCommand,
        vehicleState: VehicleState,
        explicitConfirmation: String,
    ): Result<ExecutionPermit> = runCatching {
        validatePolicy(command, vehicleState)
        require(explicitConfirmation == command.description) {
            "La conferma esplicita non corrisponde alla descrizione del comando"
        }
        ExecutionPermit(
            id = UUID.randomUUID(),
            commandFingerprint = fingerprint(command),
            expiresAt = now().plusSeconds(30),
        )
    }

    suspend fun execute(
        command: VehicleCommand,
        vehicleState: VehicleState,
        permit: ExecutionPermit,
    ): CommandResult {
        val validation = runCatching {
            validatePolicy(command, vehicleState)
            require(now().isBefore(permit.expiresAt)) { "Autorizzazione scaduta" }
            require(permit.commandFingerprint == fingerprint(command)) {
                "L'autorizzazione appartiene a un comando differente"
            }
        }
        if (validation.isFailure) {
            return CommandResult.Blocked(validation.exceptionOrNull()?.message ?: "Comando bloccato")
        }
        return CommandResult.Executed(transport.transact(buildCommand(command)))
    }

    private fun validatePolicy(command: VehicleCommand, vehicleState: VehicleState) {
        require(policy.writeEnabled) { "Le funzioni di scrittura sono disabilitate" }
        require(command.serviceId in policy.allowedServiceIds) {
            "Il servizio 0x${command.serviceId.toString(16)} non è autorizzato"
        }
        require(command.risk.ordinal <= policy.maximumRisk.ordinal) {
            "Il rischio del comando supera la soglia configurata"
        }
        if (policy.requireStationaryVehicle) {
            val speed = requireNotNull(vehicleState.speedKph) {
                "Velocità veicolo non disponibile: esecuzione negata"
            }
            require(speed <= 0.5) { "Il veicolo deve essere fermo" }
        }
    }

    private fun buildCommand(command: VehicleCommand): String = buildString {
        append(command.serviceId.toString(16).padStart(2, '0'))
        command.payload.forEach { byte ->
            append((byte.toInt() and 0xFF).toString(16).padStart(2, '0'))
        }
    }.uppercase()

    private fun fingerprint(command: VehicleCommand): String =
        "${command.serviceId}:${command.payload.joinToString { (it.toInt() and 0xFF).toString() }}:${command.description}"
}
