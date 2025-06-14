package com.jomar.audiorecorder.audio_recorder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jomar.audiorecorder.audio_recorder.components.EmptyPlaceHolder
import com.jomar.audiorecorder.audio_recorder.components.RecordedAudioList
import com.jomar.audiorecorder.audio_recorder.components.RecordingCard
import java.io.File

@Composable
fun AudioRecorderScreen(
    paddingValues: PaddingValues,
    statusMessage: String,
    hasPermission: Boolean,
    isRecording: Boolean,
    isPlaying: Boolean,
    playbackProgress: Float,
    onPermissionRequest: () -> Unit,
    audioFiles: List<File>,
    currentPlayingFile: String?,
    onDeleteFile: (File) -> Unit,
    stopRecording: () -> Unit,
    onStartPlayback: (File) -> Unit,
    startRecording: () -> Unit,
    onStopPlayback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            RecordingCard(
                statusMessage,
                hasPermission,
                isRecording,
                stopRecording,
                startRecording,
                isPlaying,
                onStopPlayback,
                playbackProgress,
                onPermissionRequest
            )
        }
        if (audioFiles.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                RecordedAudioList(
                    audioFiles = audioFiles,
                    currentPlayingFile = currentPlayingFile,
                    isPlaying = isPlaying,
                    onDeleteFile = onDeleteFile,
                    onStartPlayback = onStartPlayback,
                    onStopPlayback = onStopPlayback
                )
            }
        } else if (hasPermission) {
            EmptyPlaceHolder()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioRecorderScreenPreview() {
    AudioRecorderScreen(
        paddingValues = PaddingValues(16.dp),
        statusMessage = "Pressione o botão para começar a gravar",
        hasPermission = true,
        isRecording = false,
        isPlaying = false,
        playbackProgress = 0f,
        onPermissionRequest = {},
        audioFiles = emptyList(),
        currentPlayingFile = null,
        onDeleteFile = {},
        stopRecording = {},
        onStartPlayback = {},
        startRecording = {},
        onStopPlayback = {}
    )
}

