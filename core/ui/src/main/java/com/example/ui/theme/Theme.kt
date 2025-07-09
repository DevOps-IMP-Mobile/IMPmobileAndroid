package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Purple400,
    tertiary = Purple100,
    background = White,
    surface = Purple25,
    onPrimary = White,
    onSecondary = White,
    onBackground = Gray900,
    onSurface = Gray900
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,  // 이 부분 수정
        content = content
    )
}