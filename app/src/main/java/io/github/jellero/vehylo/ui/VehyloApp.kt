package io.github.jellero.vehylo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jellero.vehylo.mapping.SignalMapping
import io.github.jellero.vehylo.telemetry.TelemetryKey
import io.github.jellero.vehylo.telemetry.TelemetrySample
import java.util.Locale
import kotlinx.coroutines.flow.StateFlow

private enum class VehyloSection(val label: String, val marker: String) {
    DASHBOARD("Dashboard", "D"),
    MAPPING("Mapping", "M"),
    DIAGNOSTICS("Diagnostica", "X"),
    FEATURES("Funzioni", "+"),
}

@Composable
fun VehyloApp(
    telemetry: StateFlow<Map<TelemetryKey, TelemetrySample>>,
    mappings: StateFlow<List<SignalMapping>>,
    onSaveMapping: (SignalMapping) -> Unit,
    onRequestBluetoothPermissions: () -> Unit,
) {
    val values by telemetry.collectAsStateWithLifecycle()
    val savedMappings by mappings.collectAsStateWithLifecycle()
    var selectedSection by rememberSaveable { mutableStateOf(VehyloSection.DASHBOARD) }
    val colorScheme = darkColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    VehyloSection.entries.forEach { section ->
                        NavigationBarItem(
                            selected = section == selectedSection,
                            onClick = { selectedSection = section },
                            icon = { Text(section.marker, fontWeight = FontWeight.Bold) },
                            label = { Text(section.label) },
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedSection) {
                VehyloSection.DASHBOARD -> Dashboard(
                    values = values,
                    onRequestBluetoothPermissions = onRequestBluetoothPermissions,
                    modifier = Modifier.padding(padding),
                )

                VehyloSection.MAPPING -> MappingWizardScreen(
                    savedMappings = savedMappings,
                    onSaveMapping = onSaveMapping,
                    modifier = Modifier.padding(padding),
                )

                VehyloSection.DIAGNOSTICS -> DiagnosticsOverviewScreen(
                    modifier = Modifier.padding(padding),
                )

                VehyloSection.FEATURES -> FeatureHubScreen(
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun Dashboard(
    values: Map<TelemetryKey, TelemetrySample>,
    onRequestBluetoothPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val roll = values[TelemetryKey.ROLL]?.value ?: 0.0
    val pitch = values[TelemetryKey.PITCH]?.value ?: 0.0
    val preferredKeys = listOf(
        TelemetryKey.ENGINE_RPM,
        TelemetryKey.VEHICLE_SPEED,
        TelemetryKey.COOLANT_TEMPERATURE,
        TelemetryKey.ENGINE_OIL_TEMPERATURE,
        TelemetryKey.TRANSMISSION_OIL_TEMPERATURE,
        TelemetryKey.ENGINE_LOAD,
        TelemetryKey.THROTTLE_POSITION,
        TelemetryKey.INTAKE_MANIFOLD_PRESSURE,
        TelemetryKey.FUEL_LEVEL,
        TelemetryKey.CONTROL_MODULE_VOLTAGE,
    )
    val metricKeys = preferredKeys.filter(values::containsKey) +
        values.keys
            .filterNot { it in preferredKeys || it == TelemetryKey.ROLL || it == TelemetryKey.PITCH }
            .sortedBy { it.label }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Vehylo", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(
                "Telemetria veicolo · parametri standard e mapping personalizzati",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            InclinometerCard(roll = roll, pitch = pitch)
        }
        items(metricKeys, key = TelemetryKey::id) { key ->
            MetricCard(key = key, sample = values[key])
        }
        item {
            Button(
                onClick = onRequestBluetoothPermissions,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Autorizza dispositivi Bluetooth")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MetricCard(key: TelemetryKey, sample: TelemetrySample?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(key.label, style = MaterialTheme.typography.titleMedium)
                Text(
                    sample?.source?.name ?: "NON DISPONIBILE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = sample?.let {
                    if (key.unit.isBlank()) format(it.value) else "${format(it.value)} ${key.unit}"
                } ?: "—",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun InclinometerCard(roll: Double, pitch: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {}
                Canvas(modifier = Modifier.size(112.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f),
                        radius = size.minDimension / 2,
                        center = center,
                    )
                    rotate(roll.toFloat(), pivot = center) {
                        drawLine(
                            color = androidx.compose.ui.graphics.Color.White,
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 6f,
                        )
                    }
                    val pitchOffset = (pitch.coerceIn(-30.0, 30.0) / 30.0 * center.y).toFloat()
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.White,
                        radius = 9f,
                        center = Offset(center.x, center.y + pitchOffset),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Inclinometro", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Text("Rollio  ${format(roll)}°", style = MaterialTheme.typography.titleMedium)
                Text("Pitch  ${format(pitch)}°", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun format(value: Double): String = String.format(Locale.ITALY, "%.1f", value)
