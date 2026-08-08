package com.transistor.radio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val streamUrl: String,
    val iconUrl: String? = null,
    val tags: String = "",
    val country: String? = null,
    val bitrate: Int = 0,
    val codec: String? = null,
    val isFavorite: Boolean = false,
    val lastPlayedTimestamp: Long = 0L,
    val category: String? = null
)
