package io.github.jellero.vehylo.obd

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BluetoothClassicObdTransport(
    private val context: Context,
    private val deviceAddress: String,
) : ObdTransport {
    private var socket: BluetoothSocket? = null

    @SuppressLint("MissingPermission")
    override suspend fun connect() = withContext(Dispatchers.IO) {
        checkConnectPermission()
        val manager = context.getSystemService(BluetoothManager::class.java)
        val adapter = requireNotNull(manager.adapter) { "Bluetooth non disponibile" }
        val device = adapter.getRemoteDevice(deviceAddress)
        socket = device.createRfcommSocketToServiceRecord(SPP_UUID).also { it.connect() }
    }

    override suspend fun transact(command: String): String = withContext(Dispatchers.IO) {
        val activeSocket = checkNotNull(socket) { "Trasporto OBD non connesso" }
        activeSocket.outputStream.write("${command.trim()}\r".toByteArray(Charsets.US_ASCII))
        activeSocket.outputStream.flush()

        val output = ByteArrayOutputStream()
        val buffer = ByteArray(128)
        while (true) {
            val count = activeSocket.inputStream.read(buffer)
            check(count >= 0) { "Connessione OBD terminata" }
            output.write(buffer, 0, count)
            if (buffer.take(count).any { it.toInt().toChar() == '>' }) break
        }
        output.toString(Charsets.US_ASCII.name())
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        socket?.close()
        socket = null
    }

    private fun checkConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            check(
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED
            ) { "Permesso BLUETOOTH_CONNECT non concesso" }
        }
    }

    private companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
