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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Chame enableEdgeToEdge ANTES de super.onCreate ou setContent
        // Usando SystemBarStyle.auto para que o sistema tente determinar a cor dos ícones.
        // As lambdas para isAppearanceLight... foram removidas pois causavam erro de referência.
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
            // Não precisamos mais do Accompanist SystemUiController nem do DisposableEffect para a window
            // se enableEdgeToEdge estiver configurado corretamente.

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
            var currentScreen by remember { mutableStateOf<String>(Routes.INICIAL) }
            var currentProdutoId by remember { mutableStateOf<String?>(null) }

            val appViewModel: AppViewModel = remember { AppViewModel() }
            val produtoRepository = remember { ProdutoRepositoryImpl() }
            val produtoListaViewModel: ProdutoListaViewModel = remember(produtoRepository) {
                ProdutoListaViewModel(produtoRepository)
            }

            var isImageOverlayVisible by remember { mutableStateOf(false) }

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
                        currentScreen = Routes.DETALHES_BASE
                    },
                    onNavigateBack = { currentScreen = Routes.INICIAL }
                )
                Routes.DETALHES_BASE -> {
                    currentProdutoId?.let { prodId ->
                        val detalhesViewModel: DetalhesProdutoViewModel = remember(prodId, produtoRepository) {
                            DetalhesProdutoViewModel(produtoRepository, prodId)
                        }

                        DetalhesProdutoScreen(
                            viewModel = detalhesViewModel,
                            onNavigateBack = {
                                appViewModel.clearAllFilters()
                                produtoListaViewModel.requestScrollToTop()
                                currentScreen = Routes.LISTA
                                currentProdutoId = null
                                isImageOverlayVisible = false
                            },
                            isImageOverlayVisible = isImageOverlayVisible,
                            onImageZoomRequested = { isImageOverlayVisible = true },
                            onImageDismiss = { isImageOverlayVisible = false }
                        )
                    } ?: run {
                        currentScreen = Routes.LISTA // Fallback
                    }
                }
                Routes.FALE_CONOSCO -> TelaFaleConoscoScreen(
                    onNavigateBack = { currentScreen = Routes.INICIAL }
                )
            }
        }
    }
}