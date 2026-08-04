package io.github.jellero.vehylo.commands

import io.github.jellero.vehylo.obd.ObdTransport
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleCommandGatewayTest {
    private val transport = object : ObdTransport {
        override suspend fun connect() = Unit
        override suspend fun transact(command: String): String = "OK"
        override suspend fun disconnect() = Unit
    }

    @Test
    fun writingIsDisabledByDefault() {
        val gateway = VehicleCommandGateway(transport)
        val result = gateway.arm(
            command = VehicleCommand.ClearDiagnosticInformation,
            vehicleState = VehicleState(speedKph = 0.0, ignitionOn = true),
            explicitConfirmation = VehicleCommand.ClearDiagnosticInformation.description,
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun enabledPolicyStillRequiresStationaryVehicle() {
        val gateway = VehicleCommandGateway(
            transport = transport,
            policy = CommandPolicy(
                writeEnabled = true,
                allowedServiceIds = setOf(0x04),
                maximumRisk = CommandRisk.HIGH,
            ),
        )
        val result = gateway.arm(
            command = VehicleCommand.ClearDiagnosticInformation,
            vehicleState = VehicleState(speedKph = 20.0, ignitionOn = true),
            explicitConfirmation = VehicleCommand.ClearDiagnosticInformation.description,
        )
        assertTrue(result.isFailure)
    }
}
