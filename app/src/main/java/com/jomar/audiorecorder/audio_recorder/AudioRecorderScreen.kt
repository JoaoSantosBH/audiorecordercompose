package com.jomar.audiorecorder.audio_recorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        // Seção de gravação
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (hasPermission) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (isRecording) {
                                    stopRecording()
                                } else {
                                    startRecording()
                                }
                            },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Parar gravação" else "Iniciar gravação",
                                modifier = Modifier.size(36.dp),
                                tint = Color.White
                            )
                        }

                        if (isPlaying && !isRecording) {
                            IconButton(
                                onClick = { onStopPlayback() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Parar reprodução",
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isRecording) "Toque para parar" else "Toque para gravar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Barra de progresso da reprodução
                    if (isPlaying && playbackProgress > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = ProgressIndicatorDefaults.linearColor,
                            trackColor = ProgressIndicatorDefaults.linearTrackColor,
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onPermissionRequest()
                        }
                    ) {
                        Text("Conceder Permissão de Áudio")
                    }
                }
            }
        }

        // Lista de arquivos gravados
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
private fun EmptyPlaceHolder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhuma gravação encontrada\nGrave seu primeiro áudio!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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


@Composable
fun AudioFileItem(
    file: File,
    isPlaying: Boolean,
    isRecording: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = formatFileDate(file.lastModified()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = if (isPlaying) onStop else onPlay,
                    enabled = !isRecording
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                        tint = if (isRecording)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    enabled = !isRecording && !isPlaying
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar",
                        tint = if (isRecording || isPlaying)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
