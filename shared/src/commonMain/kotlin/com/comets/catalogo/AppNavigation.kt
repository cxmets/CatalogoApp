package com.comets.catalogo

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.comets.catalogo.ui.theme.CatalogoAppTheme // Importa o tema do commonMain


@Composable
fun AppNavigation() {
    CatalogoAppTheme {
        Navigator(
            screen = TelaInicialScreen // <<< CORREÇÃO: Removidos os parênteses ()
        ) { navigator ->
            SlideTransition(navigator)
        }
    }
}