package com.transistor.radio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}
