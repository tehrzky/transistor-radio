package com.transistor.radio

import android.content.Context
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Installs a global uncaught-exception handler so that whatever crashes the app,
 * the full stack trace is saved to disk before the process dies. On the next
 * launch, MainActivity checks for a saved crash and shows it on-screen — no
 * adb/logcat access needed to see what actually went wrong.
 */
object CrashHandler {
    private const val PREFS_NAME = "crash_log_prefs"
    private const val KEY_CRASH_LOG = "last_crash_log"

    fun install(context: Context) {
        val appContext = context.applicationContext
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val stringWriter = StringWriter()
                throwable.printStackTrace(PrintWriter(stringWriter))
                val fullLog = buildString {
                    append("Crashed on thread: ${thread.name}\n\n")
                    append(stringWriter.toString())
                }
                appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_CRASH_LOG, fullLog)
                    .commit() // synchronous on purpose: the process may die immediately after this
            } catch (_: Throwable) {
                // Never let the crash handler itself throw and mask the original crash.
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun getLastCrash(context: Context): String? {
        return context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CRASH_LOG, null)
    }

    fun clearLastCrash(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_CRASH_LOG)
            .apply()
    }
}
