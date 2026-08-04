package io.github.jellero.vehylo.bluetooth

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Inclination(
    val rollDegrees: Double,
    val pitchDegrees: Double,
)

object InclinationPayloadParser {
    /**
     * Formato MVP configurabile: due Float32 little-endian, prima rollio e poi beccheggio.
     * Il decoder va sostituito o esteso quando è noto il protocollo del sensore reale.
     */
    fun parseFloat32LittleEndian(payload: ByteArray): Inclination {
        require(payload.size >= 8) { "Il payload dell'inclinometro deve contenere almeno 8 byte" }
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return Inclination(
            rollDegrees = buffer.float.toDouble(),
            pitchDegrees = buffer.float.toDouble(),
        )
    }
}
