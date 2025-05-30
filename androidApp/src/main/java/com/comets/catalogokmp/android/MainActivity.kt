package com.comets.catalogokmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.comets.catalogokmp.ui.theme.CatalogoKmpTheme
import com.comets.catalogokmp.navigation.TelaInicialScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb(),
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalOnBackPressedDispatcherOwner provides this
            ) {
                AppEntry()
            }
        }
    }
}

@Composable
fun AppEntry() {
    CatalogoKmpTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Navigator(screen = TelaInicialScreen) { navigator ->
                FadeTransition(
                    navigator = navigator,
                    animationSpec = tween(durationMillis = 250)
                ) { screen ->
                    screen.Content()
                }
            }
        }
    }
}