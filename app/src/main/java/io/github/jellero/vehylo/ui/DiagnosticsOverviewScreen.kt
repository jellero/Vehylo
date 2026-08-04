package io.github.jellero.vehylo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DiagnosticsOverviewScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Diagnostica", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Servizi generici OBD-II in sola lettura",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            FeatureCard(
                title = "Letture disponibili",
                body = "Stato MIL, numero DTC confermati, codici memorizzati (Mode 03), pending (Mode 07) e permanenti (Mode 0A).",
            )
        }
        item {
            FeatureCard(
                title = "Profili diagnostici",
                body = "Ogni profilo veicolo può dichiarare centraline, mapping, capacità OBD/UDS e servizi supportati senza inserirli direttamente nella UI.",
            )
        }
        item {
            FeatureCard(
                title = "Scrittura bloccata",
                body = "Il gateway comandi è presente ma disabilitato per default. Richiede allowlist del servizio, veicolo fermo, limite di rischio e conferma esplicita a validità breve.",
            )
        }
        item {
            FeatureCard(
                title = "Esclusioni di sicurezza",
                body = "Nessun bypass SecurityAccess, immobilizer, chiavi, firmware flashing o comando proprietario viene abilitato automaticamente.",
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun FeatureCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(body)
        }
    }
}
