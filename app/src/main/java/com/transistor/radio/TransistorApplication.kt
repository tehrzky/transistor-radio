package com.transistor.radio

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class TransistorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("TransistorCrash", "Uncaught exception on thread ${thread.name}", throwable)
            try {
                val crashFile = File(filesDir, "crash_log.txt")
                crashFile.writeText(
                    "Time: ${System.currentTimeMillis()}
" +
                    "Thread: ${thread.name}
" +
                    "Exception: ${throwable.javaClass.name}
" +
                    "Message: ${throwable.message}

" +
                    throwable.stackTraceToString()
                )
            } catch (_: Exception) { }
            // Re-throw so the system still reports the crash
            throw throwable
        }
    }
}
