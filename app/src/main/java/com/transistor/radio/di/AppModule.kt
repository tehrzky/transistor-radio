package com.transistor.radio.di

import android.content.Context
import androidx.room.Room
import com.transistor.radio.data.local.AppDatabase
import com.transistor.radio.data.local.StationDao
import com.transistor.radio.data.remote.RadioBrowserApi
import com.transistor.radio.data.remote.RadioBrowserRetrofitClient
import com.transistor.radio.data.repository.StationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "transistor_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStationDao(database: AppDatabase): StationDao = database.stationDao()

    @Provides
    @Singleton
    fun provideRadioBrowserApi(): RadioBrowserApi = RadioBrowserRetrofitClient.api

    @Provides
    @Singleton
    fun provideStationRepository(
        stationDao: StationDao,
        api: RadioBrowserApi
    ): StationRepository = StationRepository(stationDao, api)
}
