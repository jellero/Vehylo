package io.github.jellero.vehylo.features

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class FeatureRequestStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun list(): List<FeatureRequest> = runCatching {
        val array = JSONArray(preferences.getString(KEY_REQUESTS, "[]") ?: "[]")
        buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                runCatching {
                    FeatureRequest(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        description = item.getString("description"),
                        createdAtEpochMillis = item.getLong("createdAtEpochMillis"),
                    )
                }.onSuccess(::add)
            }
        }.sortedByDescending { it.createdAtEpochMillis }
    }.getOrDefault(emptyList())

    fun add(title: String, description: String): FeatureRequest {
        val request = FeatureRequest(title = title.trim(), description = description.trim())
        save(list() + request)
        return request
    }

    private fun save(requests: List<FeatureRequest>) {
        val array = JSONArray()
        requests.forEach { request ->
            array.put(
                JSONObject()
                    .put("id", request.id)
                    .put("title", request.title)
                    .put("description", request.description)
                    .put("createdAtEpochMillis", request.createdAtEpochMillis)
            )
        }
        preferences.edit().putString(KEY_REQUESTS, array.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "vehylo_features"
        const val KEY_REQUESTS = "requests"
    }
}
