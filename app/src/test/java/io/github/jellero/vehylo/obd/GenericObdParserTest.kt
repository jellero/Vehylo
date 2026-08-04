package io.github.jellero.vehylo.obd

import org.junit.Assert.assertEquals
import org.junit.Test

class GenericObdParserTest {
    @Test
    fun parsesEngineRpm() {
        assertEquals(1726.0, GenericObdParser.parse(ObdPid.EngineRpm, "41 0C 1A F8 >"), 0.001)
    }

    @Test
    fun parsesVehicleSpeed() {
        assertEquals(40.0, GenericObdParser.parse(ObdPid.VehicleSpeed, "41 0D 28"), 0.001)
    }

    @Test
    fun parsesCoolantTemperature() {
        assertEquals(83.0, GenericObdParser.parse(ObdPid.CoolantTemperature, "41 05 7B"), 0.001)
    }

    @Test
    fun toleratesElmEchoAndWhitespace() {
        assertEquals(50.196, GenericObdParser.parse(ObdPid.ThrottlePosition, "0111\r41 11 80\r>"), 0.01)
    }
}
