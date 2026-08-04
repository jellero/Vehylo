package io.github.jellero.vehylo.profile

import io.github.jellero.vehylo.mapping.SignalMapping

const val CURRENT_PROFILE_SCHEMA_VERSION = 1

data class VehicleIdentity(
    val manufacturer: String,
    val model: String,
    val year: Int?,
    val vin: String? = null,
)

data class DiagnosticCapabilities(
    val genericObd: Boolean = true,
    val pendingDtcs: Boolean = true,
    val permanentDtcs: Boolean = false,
    val udsReadOnly: Boolean = false,
    val writeServicesDeclared: Set<Int> = emptySet(),
)

data class VehicleProfile(
    val id: String,
    val name: String,
    val schemaVersion: Int = CURRENT_PROFILE_SCHEMA_VERSION,
    val vehicle: VehicleIdentity,
    val mappings: List<SignalMapping> = emptyList(),
    val diagnostics: DiagnosticCapabilities = DiagnosticCapabilities(),
    val notes: String = "",
) {
    init {
        require(id.isNotBlank())
        require(name.isNotBlank())
        require(schemaVersion in 1..CURRENT_PROFILE_SCHEMA_VERSION) {
            "Versione profilo non supportata: $schemaVersion"
        }
        require(mappings.map { it.key }.distinct().size == mappings.size) {
            "Ogni mapping deve avere una chiave univoca"
        }
    }

    fun withMapping(mapping: SignalMapping): VehicleProfile = copy(
        mappings = mappings.filterNot { it.key == mapping.key } + mapping
    )
}

interface VehicleProfileRepository {
    suspend fun list(): List<VehicleProfile>
    suspend fun find(id: String): VehicleProfile?
    suspend fun save(profile: VehicleProfile)
    suspend fun delete(id: String)
}

class InMemoryVehicleProfileRepository : VehicleProfileRepository {
    private val profiles = linkedMapOf<String, VehicleProfile>()

    override suspend fun list(): List<VehicleProfile> = profiles.values.toList()

    override suspend fun find(id: String): VehicleProfile? = profiles[id]

    override suspend fun save(profile: VehicleProfile) {
        profiles[profile.id] = profile
    }

    override suspend fun delete(id: String) {
        profiles.remove(id)
    }
}
