package io.github.jellero.vehylo.telemetry

import java.io.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TelemetryRepository(
    private val scope: CoroutineScope,
) : Closeable {
    private val _latest = MutableStateFlow<Map<TelemetryKey, TelemetrySample>>(emptyMap())
    val latest: StateFlow<Map<TelemetryKey, TelemetrySample>> = _latest.asStateFlow()

    private val jobs = mutableListOf<Job>()
    private val sources = mutableListOf<TelemetrySource>()

    suspend fun attach(source: TelemetrySource) {
        source.connect()
        sources += source
        jobs += scope.launch {
            source.samples().collect { sample ->
                synchronized(_latest) {
                    _latest.value = _latest.value + (sample.key to sample)
                }
            }
        }
    }

    override fun close() {
        jobs.forEach(Job::cancel)
        jobs.clear()
        runBlocking {
            sources.forEach { source -> runCatching { source.disconnect() } }
        }
        sources.clear()
    }
}
