package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.remote.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class RepoResult<out T> {
    data class Success<T>(val data: T, val isOffline: Boolean) : RepoResult<T>()
    data class Error(val message: String) : RepoResult<Nothing>()
}

data class CachedBundle(
    val city: GeoCity,
    val forecast: ForecastResponse,
    val updatedIso: String
)

class WeatherRepository(
    private val store: AppDataStore,
    private val geocoding: GeocodingApi,
    private val forecastApi: ForecastApi
) {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend fun searchCities(query: String): RepoResult<List<GeoCity>> {
        val q = query.trim()
        if (q.isBlank()) return RepoResult.Error("Enter a city name.")

        return try {
            val res = geocoding.search(name = q)
            val list = res.results ?: emptyList()
            if (list.isEmpty()) RepoResult.Error("City not found.")
            else RepoResult.Success(list, isOffline = false)
        } catch (e: IOException) {
            RepoResult.Error("No internet connection.")
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> RepoResult.Error("Too many requests (rate limit). Try later.")
                else -> RepoResult.Error("API error: ${e.code()}")
            }
        } catch (e: Exception) {
            RepoResult.Error("Unexpected error.")
        }
    }

    suspend fun getWeather(city: GeoCity, units: String): RepoResult<CachedBundle> {

        // 1) Пытаемся получить данные из сети
        try {
            val res = forecastApi.forecast(
                lat = city.latitude,
                lon = city.longitude,
                temperatureUnit = units
            )

            val updated = SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
            ).format(Date())

            store.saveCache(
                cityJson = json.encodeToString(city),
                forecastJson = json.encodeToString(res),
                updated = updated
            )

            return RepoResult.Success(
                CachedBundle(city, res, updated),
                isOffline = false
            )

        } catch (e: IOException) {
            // нет интернета → fallback
        } catch (e: HttpException) {
            // ошибка API → fallback
        } catch (e: Exception) {
            // fallback
        }

        // 2) Пробуем кэш
        val cached = readCache()
        return if (cached != null) {
            RepoResult.Success(cached, isOffline = true)
        } else {
            RepoResult.Error("Network error and no cached data.")
        }
    }

    private suspend fun readCache(): CachedBundle? {
        val cityJson = store.cachedCityJson.first()
        val forecastJson = store.cachedForecastJson.first()
        val updated = store.cachedUpdated.first()

        if (cityJson.isNullOrBlank() || forecastJson.isNullOrBlank() || updated.isNullOrBlank()) {
            return null
        }

        return try {
            val city = json.decodeFromString(GeoCity.serializer(), cityJson)
            val forecast = json.decodeFromString(ForecastResponse.serializer(), forecastJson)
            CachedBundle(city, forecast, updated)
        } catch (e: Exception) {
            null
        }
    }
}
