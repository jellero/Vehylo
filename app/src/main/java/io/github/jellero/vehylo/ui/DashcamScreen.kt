package io.github.jellero.vehylo.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.github.jellero.vehylo.dashcam.DashcamController

@Composable
fun DashcamScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val controller = remember { DashcamController(context.applicationContext) }
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var includeAudio by remember { mutableStateOf(false) }
    var ready by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Concedi il permesso fotocamera") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionGranted = result[Manifest.permission.CAMERA] == true
        includeAudio = result[Manifest.permission.RECORD_AUDIO] == true
        status = if (permissionGranted) "Preparazione fotocamera" else "Permesso fotocamera negato"
    }

    DisposableEffect(permissionGranted, lifecycleOwner) {
        if (permissionGranted) {
            controller.bind(
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                onReady = {
                    ready = true
                    status = "Dashcam pronta"
                },
                onError = { error -> status = error.message ?: "Errore fotocamera" },
            )
        }
        onDispose { controller.release() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Dashcam", style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(onClick = onBack) { Text("Indietro") }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            )
        }

        Text(status, style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Registra anche l'audio")
            Switch(
                checked = includeAudio,
                onCheckedChange = { includeAudio = it },
                enabled = permissionGranted,
            )
        }

        if (!permissionGranted) {
            Button(
                onClick = {
                    permissionLauncher.launch(
                        buildList {
                            add(Manifest.permission.CAMERA)
                            add(Manifest.permission.RECORD_AUDIO)
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }.toTypedArray()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Autorizza dashcam")
            }
        } else if (!recording) {
            Button(
                onClick = {
                    runCatching {
                        controller.startRecording(includeAudio = includeAudio) { event ->
                            when (event) {
                                is VideoRecordEvent.Start -> {
                                    recording = true
                                    status = "Registrazione in corso"
                                }
                                is VideoRecordEvent.Finalize -> {
                                    recording = false
                                    status = if (event.hasError()) {
                                        "Registrazione terminata con errore ${event.error}"
                                    } else {
                                        "Video salvato in Movies/Vehylo"
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }.onFailure { error -> status = error.message ?: "Impossibile avviare la registrazione" }
                },
                enabled = ready,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Avvia registrazione")
            }
        } else {
            Button(
                onClick = { controller.stopRecording() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Ferma e salva")
            }
        }

        Text(
            "Questa prima versione registra mentre la schermata è aperta. Registrazione ciclica e protezione automatica dei clip richiederanno un servizio dedicato.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
