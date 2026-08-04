package io.github.jellero.vehylo.ui

import android.content.Intent
import android.net.Uri
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
import io.github.jellero.vehylo.features.FeatureCatalog
import io.github.jellero.vehylo.features.FeatureRequestStore
import io.github.jellero.vehylo.features.FeatureStatus
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private enum class FeaturePage {
    HOME,
    WEB_RADIO,
    DASHCAM,
}

@Composable
fun FeatureHubScreen(modifier: Modifier = Modifier) {
    var page by remember { mutableStateOf(FeaturePage.HOME) }
    when (page) {
        FeaturePage.HOME -> FeatureHome(
            onOpenRadio = { page = FeaturePage.WEB_RADIO },
            onOpenDashcam = { page = FeaturePage.DASHCAM },
            modifier = modifier,
        )
        FeaturePage.WEB_RADIO -> WebRadioScreen(
            onBack = { page = FeaturePage.HOME },
            modifier = modifier,
        )
        FeaturePage.DASHCAM -> DashcamScreen(
            onBack = { page = FeaturePage.HOME },
            modifier = modifier,
        )
    }
}

@Composable
private fun FeatureHome(
    onOpenRadio: () -> Unit,
    onOpenDashcam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val requestStore = remember { FeatureRequestStore(context.applicationContext) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var savedRequests by remember { mutableStateOf(requestStore.list()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Funzioni", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Moduli utili e richieste estendibili dall'utente",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(FeatureCatalog.builtIns, key = { it.id }) { feature ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(feature.title, style = MaterialTheme.typography.titleMedium)
                        Text(statusLabel(feature.status), style = MaterialTheme.typography.labelMedium)
                    }
                    Text(feature.description, style = MaterialTheme.typography.bodyMedium)
                    when (feature.id) {
                        "webradio" -> Button(onClick = onOpenRadio) { Text("Apri radio") }
                        "dashcam" -> Button(onClick = onOpenDashcam) { Text("Apri dashcam") }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Richiedi una funzione", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Descrivi il caso d'uso e i dispositivi coinvolti. La richiesta viene salvata sul telefono e può essere aperta come issue GitHub.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titolo") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Cosa deve fare") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = {
                            runCatching { requestStore.add(title, description) }
                                .onSuccess {
                                    feedback = "Richiesta salvata"
                                    savedRequests = requestStore.list()
                                    title = ""
                                    description = ""
                                }
                                .onFailure { feedback = it.message }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Salva richiesta") }
                    OutlinedButton(
                        onClick = {
                            val encodedTitle = URLEncoder.encode("[Feature] $title", StandardCharsets.UTF_8.name())
                            val encodedBody = URLEncoder.encode(description, StandardCharsets.UTF_8.name())
                            val uri = Uri.parse(
                                "https://github.com/jellero/Vehylo/issues/new?title=$encodedTitle&body=$encodedBody"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        enabled = title.isNotBlank() && description.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Apri richiesta su GitHub") }
                    feedback?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        if (savedRequests.isNotEmpty()) {
            item {
                Text("Richieste salvate", style = MaterialTheme.typography.titleLarge)
            }
            items(savedRequests.take(5), key = { it.id }) { request ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(request.title, style = MaterialTheme.typography.titleMedium)
                        Text(request.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

private fun statusLabel(status: FeatureStatus): String = when (status) {
    FeatureStatus.READY -> "Disponibile"
    FeatureStatus.CONFIGURABLE -> "Configurabile"
    FeatureStatus.REQUIRES_VEHICLE_PROFILE -> "Profilo veicolo"
    FeatureStatus.PLANNED -> "Pianificata"
}
