package com.example.pip

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.PictureInPictureUiState
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.graphics.toRect
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel

const val INTENT_EXTRA_VALUE = "value"

private const val ACTION_BROADCAST_CONTROL = "com.example.pip.ACTION_BROADCAST_CONTROL"
private const val REQUEST_CODE = 1000
private const val EXTRA_CONTROL_TYPE = "CONTROL_TYPE"
private const val CONTROL_TYPE = "CUSTOM_CONTROL_TYPE"

class PictureInPictureActivity : ComponentActivity() {

    private val viewModel: PictureInPictureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("AAA onCreate")
        viewModel.value = intent.getIntExtra(INTENT_EXTRA_VALUE, 0)
        setContent {
            PictureInPictureScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        println("AAA onStart")
    }

    override fun onResume() {
        super.onResume()
        println("AAA onResume")
    }

    override fun onPause() {
        super.onPause()
        println("AAA onPause")
    }

    override fun onStop() {
        super.onStop()
        println("AAA onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("AAA onDestroy")
    }

    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            println("AAA isStashed = ${pipState.isStashed}")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            println("AAA isTransitioningToPip = ${pipState.isTransitioningToPip}")
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        println("AAA onPictureInPictureModeChanged $isInPictureInPictureMode")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        println("AAA onNewIntent")
        // New intent can be handled here,
        // see comments in AndroidManifest.xml for singleTask launchMode
        viewModel.value = intent.getIntExtra(INTENT_EXTRA_VALUE, 0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PictureInPictureScreen(
    pictureInPictureViewModel: PictureInPictureViewModel = viewModel()
) {
    PictureInPicturePreAPI12()

    if (rememberIsInPipMode()) {
        ScalablePictureContent(
            modifier = Modifier
                .fillMaxSize(),
            isInPictureInPictureMode = true,
            passedIntentValue = pictureInPictureViewModel.value,
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.shadow(5.dp),
                    title = { Text("Picture In Picture") },
                    navigationIcon = {
                        val context = LocalContext.current
                        IconButton(
                            onClick = {
                                context.findActivity().finish()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EnterPictureInPictureMode()
                ScalablePictureContent(
                    isInPictureInPictureMode = false,
                    passedIntentValue = pictureInPictureViewModel.value,
                )
            }
        }
    }

    PictureInPictureActionBroadcastReceiver()
}

@Composable
private fun PictureInPicturePreAPI12() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val onUserLeaveBehavior = Runnable {
                context.findActivity()
                    .enterPictureInPictureMode(
                        PictureInPictureParams.Builder().build()
                    )
            }

            context.findActivity().addOnUserLeaveHintListener(onUserLeaveBehavior)

            onDispose {
                context.findActivity().removeOnUserLeaveHintListener(onUserLeaveBehavior)
            }
        }
    } else {
        println("API doesn't support picture in picture")
    }
}

@Composable
fun rememberIsInPipMode(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val activity = LocalContext.current.findActivity()
        var pipMode by remember { mutableStateOf(activity.isInPictureInPictureMode) }
        DisposableEffect(activity) {
            val observer = Consumer<PictureInPictureModeChangedInfo> { info ->
                pipMode = info.isInPictureInPictureMode
            }
            activity.addOnPictureInPictureModeChangedListener(
                observer
            )
            onDispose { activity.removeOnPictureInPictureModeChangedListener(observer) }
        }
        return pipMode
    } else {
        return false
    }
}

@Composable
private fun ScalablePictureContent(
    isInPictureInPictureMode: Boolean,
    passedIntentValue: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val newModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()
            val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
            builder.setSourceRectHint(sourceRect)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setSeamlessResizeEnabled(true)
            }
            builder.setAspectRatio(Rational(sourceRect.width(), sourceRect.height()))
            // Add autoEnterEnabled for versions S and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(true)
            }
            // add actions
            builder.setActions(
                listOf(
                    RemoteAction(
                        android.graphics.drawable.Icon.createWithResource(context, R.drawable.ic_add_reaction),
                        "Custom action",
                        "Custom action",
                        PendingIntent.getBroadcast(
                            context,
                            REQUEST_CODE,
                            Intent(ACTION_BROADCAST_CONTROL)
                                .setPackage(context.packageName)
                                .putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE),
                            PendingIntent.FLAG_IMMUTABLE,
                        ),
                    )
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                builder.setTitle("Title")
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }
    } else {
        modifier
    }
    Column(
        newModifier
            .fillMaxSize()
            .background(Color.Magenta),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (isInPictureInPictureMode) "In Picture in Picture mode" else "Not in Picture in Picture mode",
            textAlign = TextAlign.Center,
            color = Color.White,
        )
        Text(
            text = "Passed intent value: $passedIntentValue",
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@Composable
private fun EnterPictureInPictureMode(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        modifier = modifier,
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.findActivity().enterPictureInPictureMode(
                    PictureInPictureParams.Builder().build()
                )
            } else {
                println("API does not support PiP")
            }
        }
    ) {
        Text(text = "Minimize to Picture in Picture mode!")
    }
}

@Composable
private fun PictureInPictureActionBroadcastReceiver() {
    if (rememberIsInPipMode()) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if ((intent == null) || (intent.action != ACTION_BROADCAST_CONTROL)) {
                        return
                    }
                    val controlType = intent.getStringExtra(EXTRA_CONTROL_TYPE)
                    println("AAA Received broadcast event controlType = $controlType")
                }
            }
            ContextCompat.registerReceiver(
                context,
                broadcastReceiver,
                IntentFilter(ACTION_BROADCAST_CONTROL),
                RECEIVER_NOT_EXPORTED,
            )
            onDispose {
                context.unregisterReceiver(broadcastReceiver)
            }
        }
    }
}

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) {
            return context
        }
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}
