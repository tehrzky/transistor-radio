package com.transistor.radio.data.repository

import com.transistor.radio.data.local.StationDao
import com.transistor.radio.data.local.toEntity
import com.transistor.radio.data.local.toStation
import com.transistor.radio.data.remote.RadioBrowserApi
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.domain.model.RadioBrowserStation
import com.transistor.radio.domain.model.Station
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val stationDao: StationDao,
    private val api: RadioBrowserApi
) {
    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.STOPPED)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun getAllStations(): Flow<List<Station>> =
        stationDao.getAllStations().map { list -> list.map { it.toStation() } }

    fun getRecentlyPlayed(): Flow<List<Station>> =
        stationDao.getRecentlyPlayed().map { list -> list.map { it.toStation() } }

    fun searchLocalStations(query: String): Flow<List<Station>> =
        stationDao.searchStations(query).map { list -> list.map { it.toStation() } }

    fun getStationCount(): Flow<Int> = stationDao.getStationCount()

    suspend fun addStation(station: Station) {
        stationDao.insertStation(station.toEntity())
    }

    suspend fun addStations(stations: List<Station>) {
        stationDao.insertStations(stations.map { it.toEntity() })
    }

    suspend fun updateStation(station: Station) {
        stationDao.updateStation(station.toEntity())
    }

    suspend fun deleteStation(station: Station) {
        stationDao.deleteStation(station.id)
    }

    suspend fun deleteAllStations() {
        stationDao.deleteAllStations()
    }

    suspend fun setCurrentStation(station: Station) {
        _currentStation.value = station
        val updated = station.copy(lastPlayedTimestamp = System.currentTimeMillis())
        stationDao.insertStation(updated.toEntity())
    }

    fun setPlaybackState(state: PlaybackState) {
        _playbackState.value = state
    }

    suspend fun searchRadioBrowser(query: String): Result<List<RadioBrowserStation>> = runCatching {
        api.searchStations(name = query, limit = 50)
    }

    suspend fun getTopVotedStations(): Result<List<RadioBrowserStation>> = runCatching {
        api.getTopVoted(limit = 50)
    }

    fun getStationsByCategory(stations: List<Station>): Map<String, List<Station>> {
        val categorized = mutableMapOf<String, MutableList<Station>>()
        val uncategorized = mutableListOf<Station>()

        stations.forEach { station ->
            if (station.tags.isNotEmpty()) {
                station.tags.forEach { tag ->
                    val key = tag.replaceFirstChar { it.uppercase() }
                    categorized.getOrPut(key) { mutableListOf() }.add(station)
                }
            } else if (station.category != null) {
                categorized.getOrPut(station.category) { mutableListOf() }.add(station)
            } else {
                uncategorized.add(station)
            }
        }

        if (uncategorized.isNotEmpty()) {
            categorized["Uncategorized"] = uncategorized
        }

        return categorized.mapValues { it.value.distinctBy { s -> s.id } }
            .toSortedMap()
    }
}
