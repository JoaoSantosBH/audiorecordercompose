package com.jomar.audiorecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jomar.audiorecorder.audio_recorder.AudioRecorderRoot
import com.jomar.audiorecorder.ui.theme.AudioRecorderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioRecorderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioRecorderRoot()
                }
            }
        }
    }
}