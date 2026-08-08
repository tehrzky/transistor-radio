package com.transistor.radio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TransistorApplication : Application() {
    override fun onCreate() {
        CrashHandler.install(this)
        super.onCreate()
    }
}
