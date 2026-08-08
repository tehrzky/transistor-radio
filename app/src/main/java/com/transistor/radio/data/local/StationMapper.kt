package com.transistor.radio.data.local

import com.transistor.radio.domain.model.Station

fun StationEntity.toStation(): Station = Station(
    id = id,
    name = name,
    streamUrl = streamUrl,
    iconUrl = iconUrl,
    tags = if (tags.isNotBlank()) tags.split(",").map { it.trim() } else emptyList(),
    country = country,
    bitrate = bitrate,
    codec = codec,
    isFavorite = isFavorite,
    lastPlayedTimestamp = lastPlayedTimestamp,
    category = category
)

fun Station.toEntity(): StationEntity = StationEntity(
    id = id,
    name = name,
    streamUrl = streamUrl,
    iconUrl = iconUrl,
    tags = tags.joinToString(","),
    country = country,
    bitrate = bitrate,
    codec = codec,
    isFavorite = isFavorite,
    lastPlayedTimestamp = lastPlayedTimestamp,
    category = category
)
