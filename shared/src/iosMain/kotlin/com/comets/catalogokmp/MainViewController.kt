package com.comets.catalogokmp

import androidx.compose.ui.window.ComposeUIViewController
import com.comets.catalogokmp.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin() // Inicializa o Koin
    return ComposeUIViewController { AppRoot() }
}