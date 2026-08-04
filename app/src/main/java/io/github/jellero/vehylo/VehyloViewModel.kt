package io.github.jellero.vehylo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jellero.vehylo.telemetry.DemoTelemetrySource
import io.github.jellero.vehylo.telemetry.TelemetryKey
import io.github.jellero.vehylo.telemetry.TelemetryRepository
import io.github.jellero.vehylo.telemetry.TelemetrySample
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehyloViewModel : ViewModel() {
    private val repository = TelemetryRepository(viewModelScope)
    val telemetry: StateFlow<Map<TelemetryKey, TelemetrySample>> = repository.latest

    init {
        viewModelScope.launch {
            repository.attach(DemoTelemetrySource())
        }
    }

    override fun onCleared() {
        repository.close()
    }
}
