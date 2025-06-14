package com.jomar.audiorecorder.audio_recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatFileDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderRoot() {

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
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            statusMessage = "Permissão de áudio necessária para gravar"
        }
    }
    val onPermissionRequest: () -> Unit =
        { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }

    fun refreshAudioFiles() {
        audioDir?.let { dir ->
            audioFiles = dir.listFiles()?.filter { it.extension == "m4a" }
                ?.sortedByDescending { it.lastModified() } ?: emptyList()
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
        try {
            if (isPlaying) {
                stopPlayback()
            }

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

    val onDeleteFile: (File) -> Unit = { deleteFile(it) }
    val stopRecording: () -> Unit = { stopRecording() }
    val onStartPlayback: (File) -> Unit = { startPlayback(it) }
    val startRecording: () -> Unit = { startRecording() }
    val onStopPlayback: () -> Unit = { stopPlayback() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gravador de Áudio") }
            )
        }
    ) { paddingValues ->
        AudioRecorderScreen(
            paddingValues,
            statusMessage,
            hasPermission,
            isRecording,
            isPlaying,
            playbackProgress,
            onPermissionRequest,
            audioFiles,
            currentPlayingFile,
            onDeleteFile,
            stopRecording,
            onStartPlayback,
            startRecording,
            onStopPlayback
        )
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