# Weather App (Android)

**Course:** Native Mobile Development (NMR 2215)  
**Assignment:** 7 — Data & Networking (API Integration)  
**Platform:** Android (Kotlin)  
**UI:** Jetpack Compose

---

## Overview

This project is a simple **Weather Application** that allows a user to search for a city and view current weather information and a forecast.  
The app fetches data from a public weather API, parses JSON responses, and caches the last successful result for offline usage.

The application follows **MVVM architecture with a Repository pattern**, separating UI, business logic, and networking.

---

## Used API

**Open-Meteo API (public, no API key required)**

Open-Meteo was chosen because:
- it does **not require an API key**
- it is free and publicly accessible
- it fully satisfies the assignment requirements

### Endpoints

**Geocoding (city search):**
https://api.open-meteo.com/v1/forecast

Parameters:
- `latitude`, `longitude` — city coordinates
- `temperature_unit` — celsius or fahrenheit
- `current_weather` — current weather data
- `daily` — daily forecast (min/max temperature, weather code)

---

## Features

- City search by name
- Current weather:
    - city name
    - temperature
    - weather condition
    - wind speed
    - humidity
    - last update time
- Forecast:
    - daily forecast (3+ days)
- Unit settings:
    - Celsius / Fahrenheit
- Offline mode:
    - last successful response is cached and shown when there is no internet
    - offline state is clearly labeled
- Error handling:
    - empty input
    - city not found
    - no internet connection
    - API/network errors

---

## Architecture

The app uses **MVVM + Repository pattern**.

### Layers

- **UI (Jetpack Compose)**
    - Screens and composables
    - Observes ViewModel state

- **ViewModel**
    - Holds UI state using StateFlow
    - Calls repository methods
    - Handles loading and error states

- **Repository**
    - Single source of truth
    - Decides whether to load data from network or cache

- **Data layer**
    - Remote: Retrofit + OkHttp
    - Local: DataStore (Preferences)

This separation improves readability, testability, and maintainability.

---

## Networking & JSON Parsing

- **Networking:** Retrofit + OkHttp
- **JSON parsing:** kotlinx.serialization
- Requests are executed asynchronously
- Errors are caught and converted into user-friendly messages

---

## Local Caching / Offline Mode

- The last successful weather response is saved locally using **DataStore**
- Cached data includes:
    - selected city
    - forecast response
    - last update timestamp
- When the network is unavailable:
    - cached data is displayed
    - UI shows an **offline (cached)** label

---

## Error Handling Decisions

The app handles the following cases gracefully:
- Empty search input → shows validation error
- City not found → shows message to the user
- No internet connection → falls back to cached data (if available)
- API errors (e.g. rate limit) → shows appropriate error message

The app never crashes due to network or input errors.

---

## How to Run the App

1. Open Android Studio → Open → select the project folder.

Wait until Gradle sync finishes.

Make sure you have:

Android SDK installed

Emulator or physical Android device (API 24+)

Press Run ▶ in Android Studio.

The app will launch on the emulator/device.

Enter a city name (e.g. Astana, Almaty, London) and press Search.
