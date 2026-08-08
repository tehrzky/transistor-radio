package com.transistor.radio.domain.model

import com.google.gson.annotations.SerializedName

data class RadioBrowserStation(
    @SerializedName("stationuuid") val stationUuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("url_resolved") val urlResolved: String,
    @SerializedName("favicon") val favicon: String?,
    @SerializedName("tags") val tags: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("countrycode") val countryCode: String?,
    @SerializedName("bitrate") val bitrate: Int,
    @SerializedName("codec") val codec: String?,
    @SerializedName("homepage") val homepage: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("votes") val votes: Int,
    @SerializedName("clickcount") val clickCount: Int
) {
    fun toStation(): Station = Station(
        id = stationUuid,
        name = name.trim(),
        streamUrl = urlResolved,
        iconUrl = favicon?.takeIf { it.isNotBlank() },
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        country = country?.takeIf { it.isNotBlank() },
        bitrate = bitrate,
        codec = codec?.takeIf { it.isNotBlank() },
        isFavorite = false,
        lastPlayedTimestamp = 0L
    )
}
