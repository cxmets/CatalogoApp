package com.comets.catalogokmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.comets.catalogokmp.ui.theme.CatalogoKmpTheme
import com.comets.catalogokmp.navigation.TelaInicialScreen // Importe a sua TelaInicialScreen do Voyager (a ser criada em commonMain)

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
            // CompositionLocalProvider para o BackPressedDispatcher ainda é útil
            // para o Voyager no Android.
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
                SlideTransition(navigator = navigator) { screen ->
                    screen.Content()
                }
            }
        }
    }
}