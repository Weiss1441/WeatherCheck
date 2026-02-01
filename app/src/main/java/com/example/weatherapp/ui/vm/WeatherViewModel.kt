package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.data.repo.CachedBundle
import com.example.weatherapp.data.repo.RepoResult
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val bundle: CachedBundle? = null,
    val isOffline: Boolean = false
)

class WeatherViewModel(
    private val repo: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state

    fun load(city: GeoCity, units: String) {
        viewModelScope.launch {
            _state.value = WeatherUiState(loading = true)

            when (val result = repo.getWeather(city, units)) {
                is RepoResult.Success -> {
                    _state.value = WeatherUiState(
                        loading = false,
                        bundle = result.data,
                        isOffline = result.isOffline
                    )
                }
                is RepoResult.Error -> {
                    _state.value = WeatherUiState(
                        loading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
