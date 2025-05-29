package com.comets.catalogokmp.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
// ScreenResultListener não é mais necessário para ProdutoListaVoyagerScreen com esta nova lógica
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

object ProdutoListaVoyagerScreen : Screen { // Não precisa mais de ScreenResultListener
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val appViewModel: AppViewModel = koinInject()
        val produtoListaViewModel: ProdutoListaViewModel = koinInject()

        ProdutoListaScreen(
            appViewModel = appViewModel,
            produtoListaViewModel = produtoListaViewModel
        )
    }
}

data class DetalhesProdutoVoyagerScreen(val produtoId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val appViewModel: AppViewModel = koinInject() // Injeta AppViewModel aqui também
        val detalhesViewModel: DetalhesProdutoViewModel = koinInject {
            parametersOf(produtoId)
        }
        DetalhesProdutoScreen(
            appViewModel = appViewModel, // Passa para o Composable da UI
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