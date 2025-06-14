package com.jomar.audiorecorder.audio_recorder.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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