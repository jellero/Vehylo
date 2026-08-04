package io.github.jellero.vehylo.features

import java.util.UUID

enum class FeatureStatus {
    READY,
    CONFIGURABLE,
    REQUIRES_VEHICLE_PROFILE,
    PLANNED,
}

data class VehyloFeature(
    val id: String,
    val title: String,
    val description: String,
    val status: FeatureStatus,
)

data class FeatureRequest(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
) {
    init {
        require(title.isNotBlank()) { "Il titolo della richiesta è obbligatorio" }
        require(description.isNotBlank()) { "La descrizione della richiesta è obbligatoria" }
    }
}

object FeatureCatalog {
    val builtIns: List<VehyloFeature> = listOf(
        VehyloFeature("webradio", "Web radio", "Stazioni configurabili e catalogo utilizzabile da Android Auto.", FeatureStatus.READY),
        VehyloFeature("dashcam", "Dashcam", "Anteprima e registrazione video sul dispositivo mentre l'app è in primo piano.", FeatureStatus.READY),
        VehyloFeature("extended-telemetry", "Telemetria estesa", "Temperature, pressioni, marcia, sterzo e segnali proprietari tramite mapping.", FeatureStatus.CONFIGURABLE),
        VehyloFeature("ble-sensors", "Sensori BLE", "Inclinometri, TPMS e sensori esterni con decoder configurabili.", FeatureStatus.CONFIGURABLE),
        VehyloFeature("alerts", "Allarmi personalizzati", "Soglie, persistenza e notifiche su parametri critici.", FeatureStatus.PLANNED),
        VehyloFeature("trip-log", "Registro viaggi", "Traccia GPS, telemetria sincronizzata ed esportazione.", FeatureStatus.PLANNED),
    )
}
