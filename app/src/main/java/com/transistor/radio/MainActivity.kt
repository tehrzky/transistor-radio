package com.transistor.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.transistor.radio.ui.components.RootScreen
import com.transistor.radio.ui.theme.TransistorRadioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashHandler.install(this)
        setContent {
            TransistorRadioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CrashLogDialog()
                    RootScreen()
                }
            }
        }
    }
}

/**
 * Shows the last saved crash (if any) as soon as the app reopens after a hard crash.
 * Lets you read and copy the real stack trace with no PC/adb needed.
 */
@Composable
private fun CrashLogDialog() {
    val context = LocalContext.current
    var crashLog by remember { mutableStateOf(CrashHandler.getLastCrash(context)) }
    val clipboardManager = LocalClipboardManager.current

    if (crashLog != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Last crash detected") },
            text = {
                Column {
                    Text("The app crashed last time it was open. Tap Copy and send this back:")
                    Box(
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = crashLog.orEmpty(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(crashLog.orEmpty()))
                }) { Text("Copy") }
            },
            dismissButton = {
                TextButton(onClick = {
                    CrashHandler.clearLastCrash(context)
                    crashLog = null
                }) { Text("Dismiss") }
            }
        )
    }
}
