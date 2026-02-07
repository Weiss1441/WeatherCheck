package com.example.weatherapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.weatherapp.data.firebase.FavoritesRepository
import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.data.remote.Network
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.ui.screens.FavoritesScreen
import com.example.weatherapp.ui.screens.SearchScreen
import com.example.weatherapp.ui.screens.SettingsScreen
import com.example.weatherapp.ui.screens.WeatherScreen
import com.example.weatherapp.ui.vm.*
import kotlinx.serialization.json.Json

private const val TAG_FAV = "FB_FAV_TEST"


private fun favoritesWriteTest() {

    Log.d(TAG_FAV, "favoritesWriteTest() is disabled")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent { AppRoot() }
    }
}

private object Routes {
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val WEATHER = "weather"
    const val FAVORITES = "favorites"
}

@Composable
private fun AppRoot() {
    val nav = rememberNavController()
    val ctx = LocalContext.current

    val store = AppDataStore(ctx)
    val repo = WeatherRepository(
        store = store,
        geocoding = Network.geocodingApi,
        forecastApi = Network.forecastApi
    )

    val settingsVm: SettingsViewModel = viewModel(factory = factory { SettingsViewModel(store) })
    val units by settingsVm.units.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(navController = nav, startDestination = Routes.SEARCH) {

            composable(Routes.SEARCH) {
                val searchVm: SearchViewModel = viewModel(factory = factory { SearchViewModel(repo) })
                SearchScreen(
                    vm = searchVm,
                    onOpenSettings = { nav.navigate(Routes.SETTINGS) },
                    onOpenFavorites = { nav.navigate(Routes.FAVORITES) },
                    onCitySelected = { city ->
                        val json = Json.encodeToString(GeoCity.serializer(), city)
                        nav.navigate("${Routes.WEATHER}?city=${Uri.encode(json)}")
                    }
                )

            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    vm = settingsVm,
                    onBack = { nav.popBackStack() }
                )
            }

            composable(
                route = "${Routes.WEATHER}?city={city}",
                arguments = listOf(navArgument("city") { type = NavType.StringType })
            ) { backStack ->
                val weatherVm: WeatherViewModel = viewModel(factory = factory { WeatherViewModel(repo) })

                val cityJson = backStack.arguments?.getString("city")
                val city = cityJson?.let { Json.decodeFromString(GeoCity.serializer(), it) }

                LaunchedEffect(city, units) {
                    if (city != null) weatherVm.load(city, units)
                }

                WeatherScreen(
                    vm = weatherVm,
                    onBack = { nav.popBackStack() },
                    onOpenSettings = { nav.navigate(Routes.SETTINGS) }
                )
            }

            composable(Routes.FAVORITES) {
                val favVm: FavoritesViewModel = viewModel(
                    factory = factory { FavoritesViewModel(FavoritesRepository()) }
                )

                FavoritesScreen(
                    vm = favVm,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
