package io.github.jellero.vehylo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jellero.vehylo.mapping.SignalMapping
import io.github.jellero.vehylo.telemetry.DemoTelemetrySource
import io.github.jellero.vehylo.telemetry.TelemetryKey
import io.github.jellero.vehylo.telemetry.TelemetryRepository
import io.github.jellero.vehylo.telemetry.TelemetrySample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehyloViewModel : ViewModel() {
    private val repository = TelemetryRepository(viewModelScope)
    val telemetry: StateFlow<Map<TelemetryKey, TelemetrySample>> = repository.latest

    private val mutableMappings = MutableStateFlow<List<SignalMapping>>(emptyList())
    val mappings: StateFlow<List<SignalMapping>> = mutableMappings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.attach(DemoTelemetrySource())
        }
    }

    fun saveMapping(mapping: SignalMapping) {
        mutableMappings.value = mutableMappings.value
            .filterNot { it.key == mapping.key } + mapping
    }

    override fun onCleared() {
        repository.close()
    }
}
