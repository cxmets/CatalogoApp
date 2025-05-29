package com.comets.catalogokmp.ui.common

import androidx.activity.compose.BackHandler as AndroidBackHandler // Renomeia para evitar conflito se PlatformBackHandler fosse no mesmo arquivo
import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    AndroidBackHandler(enabled = enabled, onBack = onBack)
}