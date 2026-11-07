package com.campuslibrary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CampusPaper = Color(0xFFF7F5F0)
val CampusSurface = Color(0xFFFFFEFA)
val CampusInk = Color(0xFF172331)
val CampusMuted = Color(0xFF64707A)
val CampusLine = Color(0xFFDEDBD2)
val CampusNavy = Color(0xFF1D3448)
val CampusRed = Color(0xFFB94A3B)
val CampusGold = Color(0xFFD3A94A)
val SuccessGreenBg = Color(0xFFDCEFE1)
val SuccessGreenFg = Color(0xFF245C43)
val DangerRedBg = Color(0xFFF5DFDA)
val DangerRedFg = Color(0xFF87392F)

@Composable
fun CampusLibraryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = libraryLightColorScheme(),
        content = content
    )
}

@Composable
fun SurfaceContent(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        content = content
    )
}

private fun libraryLightColorScheme() = lightColorScheme(
    background = CampusPaper,
    surface = CampusSurface,
    primary = CampusRed,
    onPrimary = Color.White,
    secondary = CampusNavy,
    onSecondary = Color.White,
    tertiary = CampusGold,
    outline = CampusLine,
    onSurface = CampusInk,
    onBackground = CampusInk,
    error = DangerRedFg,
    onError = Color.White
)
