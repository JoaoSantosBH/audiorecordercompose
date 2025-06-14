package com.jomar.audiorecorder.audio_recorder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun RecorderdAudioList(
    audioFiles: List<File>,
    currentPlayingFile: String?,
    isPlaying: Boolean,
    onDeleteFile: (File) -> Unit,
    onStartPlayback: (File) -> Unit,
    onStopPlayback: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Arquivos Gravados (${audioFiles.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(audioFiles) { file ->
                AudioFileItem(
                    file = file,
                    isPlaying = currentPlayingFile == file.name,
                    isRecording = isPlaying,
                    onPlay = { onStartPlayback(file) },
                    onStop = { onStopPlayback() },
                    onDelete = { onDeleteFile(file) }
                )
            }
        }
    }
}