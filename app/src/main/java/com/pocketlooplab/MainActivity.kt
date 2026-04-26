package com.pocketlooplab

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pocketlooplab.audio.AndroidLoopAudioEngine
import com.pocketlooplab.state.PocketLoopLabViewModel
import com.pocketlooplab.ui.PocketLoopLabScreen
import com.pocketlooplab.ui.theme.PocketLoopLabTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PocketLoopLabViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val engine = AndroidLoopAudioEngine(applicationContext)
        viewModel = PocketLoopLabViewModel(engine, applicationContext)

        setContent {
            PocketLoopLabTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PocketLoopLabScreen(
                        viewModel = viewModel,
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }
            }
        }
    }
}
