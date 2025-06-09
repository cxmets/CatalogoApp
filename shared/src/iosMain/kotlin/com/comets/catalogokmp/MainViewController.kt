package com.comets.catalogokmp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController {
    // Koin é inicializado no AppDelegate
    return ComposeUIViewController { AppRoot() }
}
