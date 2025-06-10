package com.comets.catalogokmp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
        }
    }
}