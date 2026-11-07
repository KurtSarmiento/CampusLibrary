package com.campuslibrary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CampusLibraryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColors(),
        content = content
    )
}

@Composable
fun SurfaceContent(content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        content()
    }
}

private fun lightColors() = lightColorScheme(
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    secondary = Color(0xFFDC004E),
    onSecondary = Color.White,
    error = Color(0xFFB3261E),
    onError = Color.White
)
