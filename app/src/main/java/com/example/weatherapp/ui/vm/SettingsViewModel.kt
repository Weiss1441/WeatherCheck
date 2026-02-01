package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.AppDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val store: AppDataStore) : ViewModel() {
    val units = store.unitsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "celsius")

    fun setUnits(newUnits: String) {
        viewModelScope.launch { store.setUnits(newUnits) }
    }
}
