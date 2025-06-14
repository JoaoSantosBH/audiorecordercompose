package com.jomar.audiorecorder.audio_recorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jomar.audiorecorder.audio_recorder.components.EmptyPlaceHolder
import com.jomar.audiorecorder.audio_recorder.components.RecorderdAudioList
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
                RecorderdAudioList(
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

@Composable
fun PermissionButton(onPermissionRequest: () -> Unit) {
    Button(
        onClick = {
            onPermissionRequest()
        }
    ) {
        Text("Conceder Permissão de Áudio")
    }
}