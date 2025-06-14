package com.jomar.audiorecorder.audio_recorder.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AudioPlayingProgressBar(playbackProgress: Float) {
    Spacer(modifier = Modifier.height(16.dp))
    LinearProgressIndicator(
        progress = { playbackProgress },
        modifier = Modifier.fillMaxWidth(),
        color = ProgressIndicatorDefaults.linearColor,
        trackColor = ProgressIndicatorDefaults.linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

@Preview
@Composable
fun AudioPlayingProgressBarPreview() {
    AudioPlayingProgressBar(playbackProgress = 0.5f)
}