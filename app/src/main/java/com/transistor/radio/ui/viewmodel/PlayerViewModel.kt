package com.transistor.radio.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.transistor.radio.data.repository.StationRepository
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.domain.model.Station
import com.transistor.radio.service.PlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val repository: StationRepository
) : AndroidViewModel(application) {

    val currentStation: StateFlow<Station?> = repository.currentStation
    val playbackState: StateFlow<PlaybackState> = repository.playbackState

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    init {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        viewModelScope.launch {
            mediaController = mediaControllerFuture?.await()
        }
    }

    fun playStation(station: Station) {
        viewModelScope.launch {
            repository.setCurrentStation(station)
            val mc = mediaController ?: return@launch
            mc.setMediaItem(MediaItem.fromUri(station.streamUrl))
            mc.prepare()
            mc.play()
        }
    }

    fun togglePlayPause() {
        val mc = mediaController ?: return
        if (mc.isPlaying) {
            mc.pause()
        } else {
            if (mc.currentMediaItem == null) {
                currentStation.value?.let { playStation(it) }
            } else {
                mc.play()
            }
        }
    }

    fun stop() {
        val mc = mediaController ?: return
        mc.stop()
        mc.clearMediaItems()
        repository.setPlaybackState(PlaybackState.STOPPED)
    }

    override fun onCleared() {
        mediaControllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        super.onCleared()
    }
}
