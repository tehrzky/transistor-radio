package com.transistor.radio.domain.model

data class Station(
    val id: String,
    val name: String,
    val streamUrl: String,
    val iconUrl: String? = null,
    val tags: List<String> = emptyList(),
    val country: String? = null,
    val bitrate: Int = 0,
    val codec: String? = null,
    val isFavorite: Boolean = false,
    val lastPlayedTimestamp: Long = 0L,
    val category: String? = null
)
