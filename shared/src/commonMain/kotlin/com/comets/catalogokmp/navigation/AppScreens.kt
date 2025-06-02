package com.comets.catalogokmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.presentation.ProdutoListaViewModel
import com.comets.catalogokmp.ui.screens.detalhesproduto.DetalhesProdutoScreen
import com.comets.catalogokmp.ui.screens.faleconosco.TelaFaleConoscoScreen
import com.comets.catalogokmp.ui.screens.inicial.TelaInicial
import com.comets.catalogokmp.ui.screens.listaprodutos.ProdutoListaScreen
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

object TelaInicialScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        TelaInicial()
    }
}

object ProdutoListaVoyagerScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val appViewModel: AppViewModel = koinInject()
        val produtoListaViewModel: ProdutoListaViewModel = koinInject()

        LaunchedEffect(key) {
            produtoListaViewModel.setScreenTransitioningOut(false)
        }

        ProdutoListaScreen(
            appViewModel = appViewModel,
            produtoListaViewModel = produtoListaViewModel
        )
    }
}

data class DetalhesProdutoVoyagerScreen(val produtoId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey + produtoId
    @Composable
    override fun Content() {
        val detalhesViewModel: DetalhesProdutoViewModel = koinInject {
            parametersOf(produtoId)
        }
        DetalhesProdutoScreen(
            viewModel = detalhesViewModel
        )
    }
}

object FaleConoscoVoyagerScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        TelaFaleConoscoScreen()
    }
}