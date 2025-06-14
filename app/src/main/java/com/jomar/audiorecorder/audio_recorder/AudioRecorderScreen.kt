package com.jomar.audiorecorder.audio_recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderScreen() {

    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var outputFile by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf("Pressione o botão para começar a gravar") }
    var audioFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var currentPlayingFile by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0f) }
    var playbackDuration by remember { mutableStateOf(0) }
    val audioDir = context.getExternalFilesDir(null)

    fun refreshAudioFiles() {
        audioDir?.let { dir ->
            audioFiles = dir.listFiles()?.filter { it.extension == "m4a" }
                ?.sortedByDescending { it.lastModified() } ?: emptyList()
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            statusMessage = "Permissão de áudio necessária para gravar"
        }
    }
    val onPermissionRequest : () -> Unit = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)}
    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
            isPlaying = false
            currentPlayingFile = null
            playbackProgress = 0f
            statusMessage = "Reprodução parada"
        } catch (e: Exception) {
            statusMessage = "Erro ao parar reprodução: ${e.message}"
        }
    }

    fun startRecording() {
        try { if (isPlaying) { stopPlayback() }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "audio_$timeStamp.m4a"
            outputFile = File(context.getExternalFilesDir(null), fileName).absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                prepare()
                start()
            }
            isRecording = true
            statusMessage = "Gravando... ${fileName}"
        } catch (e: IOException) {
            statusMessage = "Erro ao iniciar gravação: ${e.message}"
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            statusMessage = "Gravação salva!"
            refreshAudioFiles()
        } catch (e: Exception) {
            statusMessage = "Erro ao parar gravação: ${e.message}"
        }
    }

    fun startPlayback(file: File) {
        try {
            stopPlayback()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    isPlaying = true
                    currentPlayingFile = file.name
                    statusMessage = "Reproduzindo: ${file.name}"
                }
                setOnCompletionListener {
                    stopPlayback()
                }
                setOnErrorListener { _, _, _ ->
                    statusMessage = "Erro ao reproduzir áudio"
                    stopPlayback()
                    true
                }
            }
        } catch (e: Exception) {
            statusMessage = "Erro ao iniciar reprodução: ${e.message}"
        }
    }

    fun deleteFile(file: File) {
        try {
            if (currentPlayingFile == file.name) {
                stopPlayback()
            }
            if (file.delete()) {
                refreshAudioFiles()
                statusMessage = "Arquivo deletado: ${file.name}"
            } else {
                statusMessage = "Erro ao deletar arquivo"
            }
        } catch (e: Exception) {
            statusMessage = "Erro ao deletar: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gravador de Áudio") }
            )
        }
    ) { paddingValues ->
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
                                    onClick = { stopPlayback() }
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
                                    isRecording = isRecording,
                                    onPlay = { startPlayback(file) },
                                    onStop = { stopPlayback() },
                                    onDelete = { deleteFile(file) }
                                )
                            }
                        }
                    }
                }
            } else if (hasPermission) {
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
        }
    }


    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        refreshAudioFiles()
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer != null) {
            try {
                val current = mediaPlayer?.currentPosition ?: 0
                val duration = mediaPlayer?.duration ?: 0
                if (duration > 0) {
                    playbackProgress = current.toFloat() / duration.toFloat()
                    playbackDuration = duration
                }
                delay(100)
            } catch (e: Exception) {
                break
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaRecorder?.release()
            mediaPlayer?.release()
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

fun formatFileDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}


// AndroidManifest.xml - Adicione estas permissões
/*
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
*/

// build.gradle (Module: app) - Dependências necessárias
/*
dependencies {
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.compose.material3:material3:1.1.2'
    implementation 'androidx.compose.material:material-icons-extended:1.5.4'
}
*/

// MainActivity.kt - Exemplo de como usar
/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                AudioRecorderScreen()
            }
        }
    }
}
*/