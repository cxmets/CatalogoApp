package com.comets.catalogokmp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent

@Composable
actual fun PlatformSpecificSystemBarsEffect(useDarkTheme: Boolean) {
    SideEffect {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (useDarkTheme) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent
        )
    }
    // Nota: Para que isso funcione, a propriedade "View controller-based status bar appearance"
    // (UIViewControllerBasedStatusBarAppearance) no Info.plist do seu app iOS
    // deve estar definida como NO (false).
    // O controle de edge-to-edge e a cor de fundo da status bar/navigation bar
    // são geralmente tratados de forma diferente no iOS, muitas vezes através do
    // safeAreaPadding() e da cor de fundo do Composable raiz.
}
