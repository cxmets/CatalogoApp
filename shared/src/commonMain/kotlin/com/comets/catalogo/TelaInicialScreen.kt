package com.comets.catalogo

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.comets.catalogo.ui.TelaInicial // Seu Composable original

object TelaInicialScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // Seu Composable TelaInicial original, agora recebe o navigator do Voyager
        // para navegar para outras telas.
        TelaInicial(
            onNavigateToLista = { navigator.push(ProdutoListaScreen) },
            onNavigateToFaleConosco = { navigator.push(TelaFaleConoscoScreen) }
        )
    }
}