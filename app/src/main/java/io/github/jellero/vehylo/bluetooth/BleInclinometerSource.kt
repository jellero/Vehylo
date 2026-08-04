package io.github.jellero.vehylo.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.github.jellero.vehylo.telemetry.TelemetryKey
import io.github.jellero.vehylo.telemetry.TelemetrySample
import io.github.jellero.vehylo.telemetry.TelemetrySource
import io.github.jellero.vehylo.telemetry.TelemetrySourceKind
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BleInclinometerSource(
    private val context: Context,
    private val deviceAddress: String,
    private val serviceUuid: UUID,
    private val characteristicUuid: UUID,
    private val parser: (ByteArray) -> Inclination = InclinationPayloadParser::parseFloat32LittleEndian,
) : TelemetrySource {
    private var gatt: BluetoothGatt? = null
    private var connected = false

    override suspend fun connect() {
        connected = true
    }

    @SuppressLint("MissingPermission")
    override suspend fun disconnect() {
        connected = false
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @SuppressLint("MissingPermission")
    override fun samples(): Flow<TelemetrySample> = callbackFlow {
        checkConnectPermission()
        check(connected) { "Sorgente BLE non connessa" }

        val manager = context.getSystemService(BluetoothManager::class.java)
        val device = requireNotNull(manager.adapter) { "Bluetooth non disponibile" }
            .getRemoteDevice(deviceAddress)

        fun emit(payload: ByteArray) {
            runCatching { parser(payload) }.onSuccess { inclination ->
                trySend(
                    TelemetrySample(
                        key = TelemetryKey.ROLL,
                        value = inclination.rollDegrees,
                        source = TelemetrySourceKind.BLE_INCLINOMETER,
                    )
                )
                trySend(
                    TelemetrySample(
                        key = TelemetryKey.PITCH,
                        value = inclination.pitchDegrees,
                        source = TelemetrySourceKind.BLE_INCLINOMETER,
                    )
                )
            }
        }

        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                    BluetoothProfile.STATE_DISCONNECTED -> close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) return
                val characteristic = gatt.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
                    ?: run {
                        close(IllegalStateException("Caratteristica BLE non trovata"))
                        return
                    }
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic.getDescriptor(CCCD_UUID) ?: return
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    @Suppress("DEPRECATION")
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    @Suppress("DEPRECATION")
                    gatt.writeDescriptor(descriptor)
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
            ) {
                if (characteristic.uuid == characteristicUuid) emit(value)
            }

            @Deprecated("Compatibilità con API precedenti")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
            ) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    characteristic.uuid == characteristicUuid
                ) {
                    @Suppress("DEPRECATION")
                    emit(characteristic.value ?: return)
                }
            }
        }

        gatt = device.connectGatt(context, false, callback)
        awaitClose {
            gatt?.disconnect()
            gatt?.close()
            gatt = null
        }
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
        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
