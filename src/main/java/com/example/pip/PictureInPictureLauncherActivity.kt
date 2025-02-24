package com.example.pip

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PictureInPictureLauncherActivity : ComponentActivity() {

    companion object {
        var value = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Scaffold(
                containerColor = Color.Cyan,
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // PiP might be disabled on devices that have low RAM. Before your app uses PiP,
                    // check to be sure it is available
                    val hasPictureInPicture = context.packageManager
                        .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                    if (hasPictureInPicture) {
                        Content()
                    } else {
                        Text(
                            "Picture-in-picture is not supported on this device.",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        Text(
            """
                                Picture-in-picture (PiP) is a special type of multi-window mode mostly used for video playback. It lets the user watch a video in a small window pinned to a corner of the screen while navigating between apps or browsing content on the main screen.
    
                                PiP leverages the multi-window APIs made available in Android 7.0 to provide the pinned video overlay window. 
                            """.trimIndent(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                startActivity(
                    Intent(context, PictureInPictureActivity::class.java).apply {
                        putExtra(INTENT_EXTRA_VALUE, value)
                    }
                )
                value++
            }
        ) {
            Text("Launch Video Activity")
        }
    }
}
