package com.comets.catalogo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.comets.catalogo.ui.theme.CatalogoAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box // Importar Box
import androidx.compose.ui.Alignment // Importar Alignment
import androidx.compose.material3.Text // Importar Text
import androidx.compose.ui.platform.LocalContext // Importar LocalContext
import android.app.Activity // Importar Activity
import androidx.core.view.WindowCompat // Importar WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme // Importar isSystemInDarkTheme


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
    val window = (context as Activity).window
    val isDarkTheme = isSystemInDarkTheme()

    CatalogoAppTheme {
        val colorScheme = MaterialTheme.colorScheme

        // Configurar a barra de status para ser transparente via Theme XML
        SideEffect {
            // Permite que o conteúdo seja desenhado por trás da barra de status
            // Esta linha é crucial para o modo edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // A cor da barra de status é definida como transparente no Theme XML (themes.xml)
            // Removida a linha depreciada: window.statusBarColor = android.graphics.Color.TRANSPARENT

            // Ajusta a cor dos ícones da barra de status (claro ou escuro)
            // Esta linha ainda é necessária para garantir a visibilidade dos ícones
            // com base no conteúdo que aparece por trás da barra transparente.
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkTheme
        }


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            val navController = rememberNavController()

            var searchText by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("") }
            var selectedCategoria2 by remember { mutableStateOf("") }


            NavHost(
                navController = navController,
                startDestination = Routes.INICIAL
            ) {
                composable(Routes.INICIAL) {
                    TelaInicial(navController = navController)
                }
                composable(Routes.LISTA) {
                    val produtos = ProdutoRepository.getProdutos()
                    ProdutoLista(
                        produtos = produtos,
                        searchText = searchText,
                        selectedCategory = selectedCategory,
                        selectedCategoria2 = selectedCategoria2,
                        onSearchTextChanged = { searchText = it },
                        onCategorySelected = { selectedCategory = it },
                        onCategoria2Selected = { selectedCategoria2 = it },
                        navController = navController
                    )
                }
                composable(Routes.DETALHES) { backStackEntry ->
                    val codigo = backStackEntry.arguments?.getString("codigo")
                    val produto = ProdutoRepository.getProdutos().find { it.codigo == codigo }
                    if (produto != null) {
                        DetalhesProduto(produto, navController)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Produto não encontrado")
                        }
                    }
                }
            }
        }
    }
}