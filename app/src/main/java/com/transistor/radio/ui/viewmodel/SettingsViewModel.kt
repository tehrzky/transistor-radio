package com.transistor.radio.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.transistor.radio.data.repository.StationRepository
import com.transistor.radio.domain.model.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val repository: StationRepository
) : AndroidViewModel(application) {

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage

    private val gson = Gson()

    fun exportLibrary(uri: Uri) {
        viewModelScope.launch {
            try {
                val stations = repository.getAllStations().first()
                val json = gson.toJson(stations)
                getApplication<Application>().contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray())
                }
                _exportMessage.value = "Library exported successfully"
            } catch (e: Exception) {
                _exportMessage.value = "Export failed: ${e.message}"
            }
        }
    }

    fun importLibrary(uri: Uri) {
        viewModelScope.launch {
            try {
                val json = getApplication<Application>().contentResolver.openInputStream(uri)?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).readText()
                } ?: return@launch
                val type = object : TypeToken<List<Station>>() {}.type
                val stations: List<Station> = gson.fromJson(json, type)
                repository.addStations(stations)
                _exportMessage.value = "Library imported (${stations.size} stations)"
            } catch (e: Exception) {
                _exportMessage.value = "Import failed: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _exportMessage.value = null
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllStations()
        }
    }
}
