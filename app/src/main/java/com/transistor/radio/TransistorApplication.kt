package com.transistor.radio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TransistorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.install(this)
    }
}
