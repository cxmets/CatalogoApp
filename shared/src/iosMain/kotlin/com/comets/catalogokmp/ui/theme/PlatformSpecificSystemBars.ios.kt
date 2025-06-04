package com.comets.catalogokmp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent

@Composable
actual fun PlatformSpecificSystemBarsEffect(useDarkTheme: Boolean) {
    SideEffect {
        UIApplication.sharedApplication.statusBarStyle = if (useDarkTheme) {
            UIStatusBarStyleLightContent
        } else {
            UIStatusBarStyleDarkContent
        }
    }
}