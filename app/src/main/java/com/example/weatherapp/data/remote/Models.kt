package com.example.weatherapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//Geocoding (search city)
@Serializable
data class GeoResponse(
    val results: List<GeoCity>? = null
)

@Serializable
data class GeoCity(
    val id: Long? = null,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("admin1") val admin1: String? = null
)

// ---------- Forecast (current + daily 3 days) ----------
@Serializable
data class ForecastResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,

    val current: CurrentWeather? = null,
    val daily: DailyWeather? = null,

    @SerialName("current_units") val currentUnits: CurrentUnits? = null,
    @SerialName("daily_units") val dailyUnits: DailyUnits? = null
)

@Serializable
data class CurrentWeather(
    val time: String? = null,

    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("apparent_temperature") val feelsLike: Double? = null,
    @SerialName("relative_humidity_2m") val humidity: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed: Double? = null,
    @SerialName("weather_code") val weatherCode: Int? = null
)

@Serializable
data class DailyWeather(
    val time: List<String>? = null,
    @SerialName("temperature_2m_max") val tempMax: List<Double>? = null,
    @SerialName("temperature_2m_min") val tempMin: List<Double>? = null,
    @SerialName("weather_code") val weatherCode: List<Int>? = null
)

@Serializable
data class CurrentUnits(
    @SerialName("temperature_2m") val temperature: String? = null,
    @SerialName("wind_speed_10m") val windSpeed: String? = null
)

@Serializable
data class DailyUnits(
    @SerialName("temperature_2m_max") val tempMax: String? = null,
    @SerialName("temperature_2m_min") val tempMin: String? = null
)
