Weather App (Android)

Course: Native Mobile Development (NMR 2215)
Assignments: 7, 8 — Data & Networking + Firebase
Platform: Android (Kotlin)
UI: Jetpack Compose
Overview

Overview

-This project is a simple Weather Application that allows the user to:
-search for a city
-view current weather + daily forecast
-switch temperature units (Celsius / Fahrenheit)
-use cached weather data when offline
-save favorite cities using Firebase
-The application follows MVVM architecture with a Repository pattern, separating UI, business logic, networking, and storage.

## Used APIs
Open-Meteo (Weather + Forecast)
Open-Meteo was chosen because:
it does not require an API key
it is free and publicly accessible
it satisfies the assignment requirements

Parameters used:
-latitude, longitude
-temperature_unit (celsius / fahrenheit)
-current_weather=true
-daily=temperature_2m_max,temperature_2m_min,weathercode
-timezone=auto

Open-Meteo Geocoding (City Search)
Used for searching cities by name.
Geocoding endpoint:https://geocoding-api.open-meteo.com/v1/search
Parameters:
name - city name
count - max results

## Features
-Weather
-City search by name
-Current weather:
    city name
    temperature
    weather condit   ion (weather code mapping)
    wind speed
-last update time

## Forecast:
daily forecast (3+ days)
Unit settings:
    Celsius / Fahrenheit
    Offline mode (Caching)

## Offline mode (Caching)
-The last successful weather response is cached locally
-When the network is unavailable:
    cached data is displayed
    UI shows OFFLINE (cached) label

## Error handling
The app handles:
-empty input
-city not found
-no internet connection
-API/network errors

## Architecture
Layers

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
  - Cloud: Firebase Realtime Database
 
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
  - weather + forecast response  
  - last update timestamp  

---

# Assignment 8 — Firebase Favorites

This project also implements **Firebase (Auth + Realtime Database)** to store user favorite cities.

---

## Firebase features implemented

- Firebase Anonymous Authentication  
- Firebase Realtime Database  
- Favorites screen:
  - Load favorites from Firebase  
  - Add favorite city  
  - Edit note/description  
  - Delete favorite  

---

## Firebase Setup (Required)

1. Firebase Setup & Configuration 
-Platform: Android. 
-SDK: Firebase Android SDK (Gradle-based). 
-Services used: Firebase Anonymous Authentication and Realtime Database. 
-Connection Proof: The application is successfully linked via google-services.json, 
and the database tree is initialized under the favorites/ node.

2. Authentication 
-Method: Anonymous Authentication. 
-Implementation: Upon application launch, the FavoritesViewModel triggers 
auth.signInAnonymously(). This generates a unique UID, allowing the app to 
isolate favorite cities and notes per user without requiring a manual login.

3. Data Model 
data class FavoriteCity(
    val id: String = "",
    val cityName: String = "",
    val note: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = ""
)

JSON:
"favorites": {
    "O7ZMBu7dFXRVuuInrH3NA1JVdb33": {
      "-OktQpqATehvIfqHXO0r": {
        "cityName": "adaadad",
        "createdAt": 1770490119392,
        "createdBy": "O7ZMBu7dFXRVuuInrH3NA1JVdb33",
        "id": "-OktQpqATehvIfqHXO0r",
        "note": "fdf"
      }
    }
## CRUD Operations & Real-time Updates (Assignment 8)

Favorites are stored in Firebase Realtime Database under:

Each favorite item contains:
- `id`
- `cityName`
- `note`
- `createdAt`
- `createdBy` (Firebase uid)

### Create
Users search for a city on the main screen and press the **Favorites** button to add it to Firebase.  
A new node is created under the current user’s `uid`.  
The note is saved as a default empty string (`""`) if the user does not enter it.

### Read (Real-time)
The Favorites screen loads data using a **real-time listener** (`ValueEventListener`).  
The listener continuously observes:


When data changes (added/updated/deleted), the UI updates automatically without manual refresh.

### Update
Users can edit the **note** for any saved city using an `AlertDialog`.  
This overwrites the existing record’s `note` field using the unique favorite `id`.

### Delete
Each favorite entry has a **Delete** button.  
Pressing it removes the node from the Firebase database tree:


5. Firebase Security Rules 
To restrict data access to authenticated owners only, the following rules were 
implemented:
{
  "rules": {
    "favorites": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid"
      }
    }
  }
}
These rules ensure that a user can only read and write data within their own UID node.

