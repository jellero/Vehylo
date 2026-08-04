package io.github.jellero.vehylo.ui

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.jellero.vehylo.mapping.ByteOrder
import io.github.jellero.vehylo.mapping.MappingCandidate
import io.github.jellero.vehylo.mapping.MappingInferenceEngine
import io.github.jellero.vehylo.mapping.MappingWizardSession
import io.github.jellero.vehylo.mapping.ReferenceObservation
import io.github.jellero.vehylo.mapping.SignalMapping
import io.github.jellero.vehylo.mapping.VehicleFrame
import java.util.Locale

@Composable
fun MappingWizardScreen(
    savedMappings: List<SignalMapping>,
    onSaveMapping: (SignalMapping) -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by rememberSaveable { mutableStateOf(0) }
    var key by rememberSaveable { mutableStateOf("custom_signal") }
    var label by rememberSaveable { mutableStateOf("Segnale personalizzato") }
    var unit by rememberSaveable { mutableStateOf("") }
    var frameId by rememberSaveable { mutableStateOf("7E8") }
    var startBit by rememberSaveable { mutableStateOf("0") }
    var bitLength by rememberSaveable { mutableStateOf("8") }
    var scale by rememberSaveable { mutableStateOf("1.0") }
    var offset by rememberSaveable { mutableStateOf("0.0") }
    var littleEndian by rememberSaveable { mutableStateOf(false) }
    var signed by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var learnedCandidate by remember { mutableStateOf<MappingCandidate?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Mapping segnali", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Wizard manuale e motore di autoapprendimento assistito",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Passaggio ${step + 1} di 4", style = MaterialTheme.typography.labelLarge)
                    when (step) {
                        0 -> {
                            OutlinedTextField(
                                value = key,
                                onValueChange = { key = it },
                                label = { Text("Chiave univoca") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = label,
                                onValueChange = { label = it },
                                label = { Text("Nome visualizzato") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = unit,
                                onValueChange = { unit = it },
                                label = { Text("Unità") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        1 -> {
                            OutlinedTextField(
                                value = frameId,
                                onValueChange = { frameId = it },
                                label = { Text("ID frame esadecimale") },
                                supportingText = { Text("Esempio: 7E8 oppure 18DAF110") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = startBit,
                                onValueChange = { startBit = it },
                                label = { Text("Bit iniziale") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = bitLength,
                                onValueChange = { bitLength = it },
                                label = { Text("Lunghezza in bit") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        2 -> {
                            OutlinedTextField(
                                value = scale,
                                onValueChange = { scale = it },
                                label = { Text("Scala") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = offset,
                                onValueChange = { offset = it },
                                label = { Text("Offset") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { littleEndian = !littleEndian }) {
                                    Text(if (littleEndian) "Little endian" else "Big endian")
                                }
                                Button(onClick = { signed = !signed }) {
                                    Text(if (signed) "Con segno" else "Senza segno")
                                }
                            }
                        }

                        else -> {
                            Text("$label ($key)", style = MaterialTheme.typography.titleLarge)
                            Text("Frame 0x${frameId.uppercase()} · bit $startBit + $bitLength")
                            Text("Scala $scale · offset $offset · unità ${unit.ifBlank { "—" }}")
                            Text(if (littleEndian) "Little endian" else "Big endian")
                            Text(if (signed) "Valore con segno" else "Valore senza segno")
                        }
                    }

                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(
                            onClick = { if (step > 0) step-- },
                            enabled = step > 0,
                        ) {
                            Text("Indietro")
                        }
                        Button(
                            onClick = {
                                error = null
                                if (step < 3) {
                                    step++
                                } else {
                                    runCatching {
                                        buildMapping(
                                            key = key,
                                            label = label,
                                            unit = unit,
                                            frameId = frameId,
                                            startBit = startBit,
                                            bitLength = bitLength,
                                            scale = scale,
                                            offset = offset,
                                            littleEndian = littleEndian,
                                            signed = signed,
                                        )
                                    }.onSuccess {
                                        onSaveMapping(it)
                                        step = 0
                                    }.onFailure {
                                        error = it.message ?: "Mapping non valido"
                                    }
                                }
                            },
                        ) {
                            Text(if (step == 3) "Salva mapping" else "Avanti")
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Autoapprendimento assistito", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Il motore confronta i frame registrati con un valore di riferimento e propone posizione, formato, scala e offset. La proposta deve essere validata prima del salvataggio.",
                    )
                    Button(onClick = { learnedCandidate = demoInference() }) {
                        Text("Esegui prova del motore")
                    }
                    learnedCandidate?.let { candidate ->
                        Text(
                            "Candidato: bit ${candidate.mapping.startBit}, ${candidate.mapping.bitLength} bit, " +
                                "confidenza ${formatPercent(candidate.confidence)}",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(candidate.explanation)
                        Button(onClick = { onSaveMapping(candidate.mapping) }) {
                            Text("Accetta candidato")
                        }
                    }
                }
            }
        }

        item {
            Text("Mapping salvati", style = MaterialTheme.typography.titleLarge)
        }
        if (savedMappings.isEmpty()) {
            item { Text("Nessun mapping personalizzato salvato.") }
        } else {
            items(savedMappings, key = { it.key }) { mapping ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(mapping.label, fontWeight = FontWeight.SemiBold)
                        Text(
                            "0x${mapping.frameId.toString(16).uppercase()} · bit ${mapping.startBit}/${mapping.bitLength} · " +
                                "${mapping.scale}x + ${mapping.offset} ${mapping.unit}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

private fun buildMapping(
    key: String,
    label: String,
    unit: String,
    frameId: String,
    startBit: String,
    bitLength: String,
    scale: String,
    offset: String,
    littleEndian: Boolean,
    signed: Boolean,
): SignalMapping {
    val session = MappingWizardSession()
    session.defineSignal(key = key, label = label, unit = unit)
    session.selectFrame(parseHexId(frameId))
    session.defineLayout(
        startBit = startBit.toInt(),
        bitLength = bitLength.toInt(),
        byteOrder = if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN,
        signed = signed,
    )
    session.useManualCalibration(scale.toDouble(), offset.toDouble())
    return session.complete()
}

private fun parseHexId(value: String): Long =
    value.trim().removePrefix("0x").removePrefix("0X").toLong(16)

private fun demoInference(): MappingCandidate? {
    val observations = (0..20).map { index ->
        val raw = index * 5
        val payload = byteArrayOf(0, 0, raw.toByte(), 0, 0, 0, 0, 0)
        ReferenceObservation(
            frame = VehicleFrame(id = 0x321, data = payload),
            referenceValue = raw * 0.5 + 10.0,
        )
    }
    return MappingInferenceEngine()
        .inferFromReference(
            signalKey = "learned_signal",
            signalLabel = "Segnale appreso",
            unit = "u",
            observations = observations,
            maxCandidates = 1,
        )
        .firstOrNull()
}

private fun formatPercent(value: Double): String =
    String.format(Locale.ITALY, "%.1f%%", value * 100.0)
