package io.github.jellero.vehylo.mapping

enum class ByteOrder {
    BIG_ENDIAN,
    LITTLE_ENDIAN,
}

enum class MappingOrigin {
    MANUAL,
    WIZARD,
    LEARNED,
    IMPORTED,
}

data class VehicleFrame(
    val id: Long,
    val data: ByteArray,
    val timestampNanos: Long = System.nanoTime(),
) {
    init {
        require(id >= 0) { "L'identificativo del frame non può essere negativo" }
        require(data.isNotEmpty()) { "Il frame deve contenere almeno un byte" }
    }
}

data class SignalMapping(
    val key: String,
    val label: String,
    val frameId: Long,
    val startBit: Int,
    val bitLength: Int,
    val byteOrder: ByteOrder,
    val signed: Boolean,
    val scale: Double,
    val offset: Double,
    val unit: String,
    val origin: MappingOrigin,
    val confidence: Double? = null,
) {
    init {
        require(key.isNotBlank()) { "La chiave del segnale è obbligatoria" }
        require(label.isNotBlank()) { "L'etichetta del segnale è obbligatoria" }
        require(frameId >= 0) { "L'identificativo del frame non può essere negativo" }
        require(startBit >= 0) { "startBit non può essere negativo" }
        require(bitLength in 1..63) { "bitLength deve essere compreso tra 1 e 63" }
        require(scale.isFinite()) { "La scala deve essere finita" }
        require(offset.isFinite()) { "L'offset deve essere finito" }
        require(confidence == null || confidence in 0.0..1.0) {
            "La confidenza deve essere compresa tra 0 e 1"
        }
    }

    fun decode(frame: VehicleFrame): Double {
        require(frame.id == frameId) {
            "Il frame ${frame.id} non corrisponde al mapping $frameId"
        }
        val raw = BitSignalCodec.extract(
            data = frame.data,
            startBit = startBit,
            bitLength = bitLength,
            byteOrder = byteOrder,
            signed = signed,
        )
        return raw.toDouble() * scale + offset
    }
}

object BitSignalCodec {
    fun extract(
        data: ByteArray,
        startBit: Int,
        bitLength: Int,
        byteOrder: ByteOrder,
        signed: Boolean,
    ): Long {
        require(startBit >= 0)
        require(bitLength in 1..63)
        require(startBit + bitLength <= data.size * 8) {
            "Il segnale supera la lunghezza del frame"
        }

        val unsigned = when (byteOrder) {
            ByteOrder.BIG_ENDIAN -> extractBigEndian(data, startBit, bitLength)
            ByteOrder.LITTLE_ENDIAN -> extractLittleEndian(data, startBit, bitLength)
        }
        if (!signed) return unsigned

        val signMask = 1L shl (bitLength - 1)
        return if (unsigned and signMask == 0L) {
            unsigned
        } else {
            unsigned - (1L shl bitLength)
        }
    }

    private fun extractBigEndian(data: ByteArray, startBit: Int, bitLength: Int): Long {
        var value = 0L
        repeat(bitLength) { index ->
            val absoluteBit = startBit + index
            val byteIndex = absoluteBit / 8
            val bitInByte = 7 - (absoluteBit % 8)
            val bit = (data[byteIndex].toInt() ushr bitInByte) and 1
            value = (value shl 1) or bit.toLong()
        }
        return value
    }

    private fun extractLittleEndian(data: ByteArray, startBit: Int, bitLength: Int): Long {
        var value = 0L
        repeat(bitLength) { index ->
            val absoluteBit = startBit + index
            val byteIndex = absoluteBit / 8
            val bitInByte = absoluteBit % 8
            val bit = (data[byteIndex].toInt() ushr bitInByte) and 1
            value = value or (bit.toLong() shl index)
        }
        return value
    }
}
