package com.comets.catalogokmp

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.comets.catalogokmp.model.UserThemePreference
import com.comets.catalogokmp.navigation.TelaInicialScreen
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.ui.theme.CatalogoKmpTheme
import com.comets.catalogokmp.ui.theme.PlatformSpecificSystemBarsEffect
import org.koin.compose.koinInject

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = koinInject()
    val userThemePreference by appViewModel.userThemePreference.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    val useDarkTheme = when (userThemePreference) {
        UserThemePreference.LIGHT -> false
        UserThemePreference.DARK -> true
        UserThemePreference.SYSTEM -> systemIsDark
    }

    PlatformSpecificSystemBarsEffect(useDarkTheme = useDarkTheme)

    CatalogoKmpTheme(darkTheme = useDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Navigator(screen = TelaInicialScreen) { navigator ->
                FadeTransition(
                    navigator = navigator,
                    animationSpec = tween(durationMillis = 300)
                ) { screen ->
                    screen.Content()
                }
            }
        }
    }
}