package com.comets.catalogo

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource // Necessário para R.string
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comets.catalogo.ui.theme.CatalogoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val window = (context as? Activity)?.window
    val isDarkTheme = isSystemInDarkTheme()

    val appViewModel: AppViewModel = viewModel()

    val searchText by appViewModel.searchText.collectAsState()
    val selectedTipo by appViewModel.selectedTipo.collectAsState()
    val selectedLente by appViewModel.selectedLente.collectAsState()
    val selectedHaste by appViewModel.selectedHaste.collectAsState()
    val selectedRosca by appViewModel.selectedRosca.collectAsState()

    CatalogoAppTheme {
        val colorScheme = MaterialTheme.colorScheme

        if (window != null) {
            SideEffect {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkTheme
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.INICIAL
            ) {
                composable(Routes.INICIAL) {
                    TelaInicial(navController = navController)
                }
                composable(Routes.LISTA) {
                    ProdutoLista(
                        searchText = searchText,
                        selectedTipo = selectedTipo,
                        selectedLente = selectedLente,
                        selectedHaste = selectedHaste,
                        selectedRosca = selectedRosca,
                        onSearchTextChanged = { appViewModel.onSearchTextChanged(it) },
                        onTipoSelected = { appViewModel.onTipoSelected(it) },
                        onLenteSelected = { appViewModel.onLenteSelected(it) },
                        onHasteSelected = { appViewModel.onHasteSelected(it) },
                        onRoscaSelected = { appViewModel.onRoscaSelected(it) },
                        onClearAllFiltersAndSearch = { appViewModel.clearAllFilters() },
                        navController = navController
                    )
                }
                composable(Routes.DETALHES) { backStackEntry ->
                    val codigo = backStackEntry.arguments?.getString("codigo")
                    val produto = remember(context, codigo) {
                        codigo?.let { ProdutoRepository.getProdutoPorCodigo(context, it) }
                    }
                    if (produto != null) {
                        DetalhesProduto(produto, navController)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = R.string.produto_nao_encontrado))
                        }
                    }
                }
                composable(Routes.FALE_CONOSCO) {
                    TelaFaleConosco(navController = navController)
                }
            }
        }
    }
}