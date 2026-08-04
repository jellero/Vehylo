package io.github.jellero.vehylo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import io.github.jellero.vehylo.ui.VehyloApp

class MainActivity : ComponentActivity() {
    private val viewModel: VehyloViewModel by viewModels()

    private val requestBluetoothPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehyloApp(
                telemetry = viewModel.telemetry,
                mappings = viewModel.mappings,
                onSaveMapping = viewModel::saveMapping,
                onRequestBluetoothPermissions = ::requestBluetoothPermissions,
            )
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        requestBluetoothPermissions.launch(permissions)
    }
}
