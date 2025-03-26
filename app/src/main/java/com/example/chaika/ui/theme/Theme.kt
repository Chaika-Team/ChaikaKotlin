package com.example.chaika.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ProductTheme (content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = RedRZD,
            surface = BottomBarBackground
        ),
        content = content
    )
}