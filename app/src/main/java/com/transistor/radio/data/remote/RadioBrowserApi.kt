package com.transistor.radio.data.remote

import com.transistor.radio.domain.model.RadioBrowserStation
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserApi {

    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("tag") tag: String? = null,
        @Query("country") country: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioBrowserStation>

    @GET("json/stations/bytag/{tag}")
    suspend fun getStationsByTag(
        @retrofit2.http.Path("tag") tag: String,
        @Query("limit") limit: Int = 50
    ): List<RadioBrowserStation>

    @GET("json/stations/topvote")
    suspend fun getTopVoted(
        @Query("limit") limit: Int = 50
    ): List<RadioBrowserStation>
}
