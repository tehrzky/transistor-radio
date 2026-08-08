package com.transistor.radio.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Query("SELECT * FROM stations ORDER BY lastPlayedTimestamp DESC, name ASC")
    fun getAllStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE lastPlayedTimestamp > 0 ORDER BY lastPlayedTimestamp DESC LIMIT 20")
    fun getRecentlyPlayed(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE id = :id")
    suspend fun getStationById(id: String): StationEntity?

    @Query("SELECT * FROM stations WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchStations(query: String): Flow<List<StationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: StationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    @Update
    suspend fun updateStation(station: StationEntity)

    @Query("DELETE FROM stations WHERE id = :id")
    suspend fun deleteStation(id: String)

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()

    @Query("SELECT COUNT(*) FROM stations")
    fun getStationCount(): Flow<Int>
}
