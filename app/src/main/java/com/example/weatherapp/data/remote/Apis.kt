package com.example.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en"
    ): GeoResponse
}

interface ForecastApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,

        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m",

        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min",

        @Query("forecast_days") forecastDays: Int = 3,
        @Query("timezone") timezone: String = "auto",

        @Query("temperature_unit") temperatureUnit: String = "celsius"
    ): ForecastResponse
}
