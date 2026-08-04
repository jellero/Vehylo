package io.github.jellero.vehylo.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.jellero.vehylo.radio.RadioStation
import io.github.jellero.vehylo.radio.RadioStationStore
import io.github.jellero.vehylo.radio.WebRadioService

@Composable
fun WebRadioScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val store = remember { RadioStationStore(context.applicationContext) }
    var stations by remember { mutableStateOf(store.list()) }
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Web radio", style = MaterialTheme.typography.headlineMedium)
                OutlinedButton(onClick = onBack) { Text("Indietro") }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Aggiungi stazione", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("URL stream HTTP/HTTPS") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = {
                            runCatching { store.upsert(name, url) }
                                .onSuccess {
                                    stations = store.list()
                                    name = ""
                                    url = ""
                                    message = "Stazione salvata"
                                }
                                .onFailure { message = it.message }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Salva stazione")
                    }
                    message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }

        items(stations, key = RadioStation::id) { station ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(station.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        station.streamUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val intent = Intent(context, WebRadioService::class.java)
                                    .setAction(WebRadioService.ACTION_PLAY)
                                    .putExtra(WebRadioService.EXTRA_STATION_ID, station.id)
                                ContextCompat.startForegroundService(context, intent)
                            }
                        ) { Text("Riproduci") }
                        OutlinedButton(
                            onClick = {
                                context.startService(
                                    Intent(context, WebRadioService::class.java)
                                        .setAction(WebRadioService.ACTION_STOP)
                                )
                            }
                        ) { Text("Stop") }
                        OutlinedButton(
                            onClick = {
                                store.remove(station.id)
                                stations = store.list()
                            }
                        ) { Text("Elimina") }
                    }
                }
            }
        }

        if (stations.isEmpty()) {
            item {
                Text(
                    "Nessuna stazione configurata. Inserisci l'URL diretto dello stream, non la pagina web dell'emittente.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
