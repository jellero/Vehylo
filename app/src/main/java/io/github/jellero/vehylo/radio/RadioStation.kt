package io.github.jellero.vehylo.radio

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
) {
    init {
        require(name.isNotBlank()) { "Il nome della stazione è obbligatorio" }
        require(streamUrl.startsWith("http://") || streamUrl.startsWith("https://")) {
            "Lo stream deve utilizzare HTTP o HTTPS"
        }
    }
}

class RadioStationStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun list(): List<RadioStation> = runCatching {
        val array = JSONArray(preferences.getString(KEY_STATIONS, "[]") ?: "[]")
        buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                val id = item.optString("id")
                val name = item.optString("name")
                val url = item.optString("streamUrl")
                runCatching { RadioStation(id = id, name = name, streamUrl = url) }
                    .onSuccess(::add)
            }
        }.sortedBy { it.name.lowercase() }
    }.getOrDefault(emptyList())

    fun upsert(name: String, streamUrl: String): RadioStation {
        val normalizedUrl = streamUrl.trim()
        val existing = list().firstOrNull { it.streamUrl == normalizedUrl }
        val station = RadioStation(
            id = existing?.id ?: UUID.randomUUID().toString(),
            name = name.trim(),
            streamUrl = normalizedUrl,
        )
        save(list().filterNot { it.id == station.id } + station)
        return station
    }

    fun remove(id: String) {
        save(list().filterNot { it.id == id })
    }

    fun find(id: String): RadioStation? = list().firstOrNull { it.id == id }

    private fun save(stations: List<RadioStation>) {
        val array = JSONArray()
        stations.forEach { station ->
            array.put(
                JSONObject()
                    .put("id", station.id)
                    .put("name", station.name)
                    .put("streamUrl", station.streamUrl)
            )
        }
        preferences.edit().putString(KEY_STATIONS, array.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "vehylo_radio"
        const val KEY_STATIONS = "stations"
    }
}
