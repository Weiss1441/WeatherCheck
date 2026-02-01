package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weather_prefs")

class AppDataStore(private val context: Context) {

    private object Keys {
        val UNITS = stringPreferencesKey("units")
        val LAST_CITY_JSON = stringPreferencesKey("last_city_json")
        val LAST_FORECAST_JSON = stringPreferencesKey("last_forecast_json")
        val LAST_UPDATED = stringPreferencesKey("last_updated")
    }

    val unitsFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.UNITS] ?: "celsius"
    }

    suspend fun setUnits(units: String) {
        context.dataStore.edit { it[Keys.UNITS] = units }
    }

    suspend fun saveCache(cityJson: String, forecastJson: String, updated: String) {
        context.dataStore.edit {
            it[Keys.LAST_CITY_JSON] = cityJson
            it[Keys.LAST_FORECAST_JSON] = forecastJson
            it[Keys.LAST_UPDATED] = updated
        }
    }

    val cachedCityJson: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_CITY_JSON] }
    val cachedForecastJson: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_FORECAST_JSON] }
    val cachedUpdated: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_UPDATED] }
}
