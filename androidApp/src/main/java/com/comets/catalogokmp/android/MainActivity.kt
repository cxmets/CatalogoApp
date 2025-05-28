package com.comets.catalogokmp.android

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.comets.catalogokmp.data.ProdutoRepositoryImpl
import com.comets.catalogokmp.navigation.Routes
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.presentation.ProdutoListaViewModel
import com.comets.catalogokmp.ui.screens.detalhesproduto.DetalhesProdutoScreen
import com.comets.catalogokmp.ui.screens.faleconosco.TelaFaleConoscoScreen
import com.comets.catalogokmp.ui.screens.inicial.TelaInicial
import com.comets.catalogokmp.ui.screens.listaprodutos.ProdutoListaScreen
import com.comets.catalogokmp.ui.theme.CatalogoKmpTheme
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Para gerenciar o Back Pressed no DetalhesProdutoScreen overlay
val LocalBackPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcherOwner?> { null }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // A inicialização do appContext já é feita na classe App

        setContent {
            // Lógica para edge-to-edge e cores da barra de status
            val window = (LocalContext.current as? Activity)?.window
            val isDarkTheme = isSystemInDarkTheme()
            if (window != null) {
                LaunchedEffect(Unit) { // Usar LaunchedEffect para executar apenas uma vez
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                }
            }
            val systemUiController = rememberSystemUiController()
            LaunchedEffect(systemUiController, isDarkTheme) {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = !isDarkTheme
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent, // Ou a cor de fundo do seu app
                    darkIcons = !isDarkTheme
                )
            }

            CompositionLocalProvider(
                LocalBackPressedDispatcher provides this
            ) {
                AppEntry()
            }
        }
    }
}

@Composable
fun AppEntry() {
    CatalogoKmpTheme { // Usa o tema do shared module
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // Sistema de navegação simples para este exemplo
            var currentScreen by remember { mutableStateOf<String>(Routes.INICIAL) }
            var currentProdutoId by remember { mutableStateOf<String?>(null) }

            // Instanciação dos ViewModels (idealmente com DI como Koin)
            val appViewModel: AppViewModel = remember { AppViewModel() }
            val produtoRepository = remember { ProdutoRepositoryImpl() }
            val produtoListaViewModel: ProdutoListaViewModel = remember { ProdutoListaViewModel(produtoRepository) }

            // Estado para o overlay de imagem em DetalhesProdutoScreen
            var isImageOverlayVisible by remember { mutableStateOf(false) }
            val backPressedDispatcher = LocalOnBackPressedDispatcher.current?.onBackPressedDispatcher


            when (currentScreen) {
                Routes.INICIAL -> TelaInicial(
                    onNavigateToLista = { currentScreen = Routes.LISTA },
                    onNavigateToFaleConosco = { currentScreen = Routes.FALE_CONOSCO }
                )
                Routes.LISTA -> ProdutoListaScreen(
                    appViewModel = appViewModel,
                    produtoListaViewModel = produtoListaViewModel,
                    onNavigateToDetalhes = { produtoId ->
                        currentProdutoId = produtoId
                        currentScreen = Routes.DETALHES_BASE // Usar DETALHES_BASE para a rota
                    },
                    onNavigateBack = { currentScreen = Routes.INICIAL }
                )
                Routes.DETALHES_BASE -> { // Usar DETALHES_BASE para a rota
                    // Precisamos garantir que currentProdutoId não seja nulo aqui.
                    // Em um sistema de navegação real, o argumento seria parte da rota.
                    currentProdutoId?.let { prodId ->
                        val detalhesViewModel: DetalhesProdutoViewModel = remember(prodId) {
                            DetalhesProdutoViewModel(produtoRepository, prodId)
                        }

                        // Lógica de Back Pressed para o overlay
                        if (isImageOverlayVisible) {
                            androidx.activity.compose.BackHandler {
                                isImageOverlayVisible = false
                            }
                        } else {
                            androidx.activity.compose.BackHandler {
                                // Limpar filtros ao voltar da tela de detalhes para a lista
                                appViewModel.clearAllFilters()
                                produtoListaViewModel.requestScrollToTop()
                                currentScreen = Routes.LISTA
                                currentProdutoId = null
                            }
                        }

                        DetalhesProdutoScreen(
                            viewModel = detalhesViewModel,
                            onNavigateBack = {
                                appViewModel.clearAllFilters()
                                produtoListaViewModel.requestScrollToTop()
                                currentScreen = Routes.LISTA
                                currentProdutoId = null
                                isImageOverlayVisible = false // Garante que o overlay feche
                            },
                            isImageOverlayVisible = isImageOverlayVisible,
                            onImageZoomRequested = { isImageOverlayVisible = true },
                            onImageDismiss = { isImageOverlayVisible = false }
                        )
                    } ?: run {
                        // Fallback se currentProdutoId for nulo (não deveria acontecer com navegação correta)
                        currentScreen = Routes.LISTA
                    }
                }
                Routes.FALE_CONOSCO -> TelaFaleConoscoScreen(
                    onNavigateBack = { currentScreen = Routes.INICIAL }
                )
            }
        }
    }
}