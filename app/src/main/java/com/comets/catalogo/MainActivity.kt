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
import androidx.navigation.compose.NavHost // Importar NavHost
import androidx.navigation.compose.composable // Importar composable
import androidx.navigation.compose.rememberNavController // Importar rememberNavController
import androidx.compose.foundation.layout.Box // Importar Box
import androidx.compose.ui.Alignment // Importar Alignment
import androidx.compose.material3.Text // Importar Text

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
    CatalogoAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background // Usa a cor de fundo do tema
        ) {
            // Mover o NavController e NavHost para cá
            val navController = rememberNavController()

            // Estado para pesquisa e filtros (pode permanecer aqui ou ir para um ViewModel futuro)
            var searchText by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("") }
            var selectedCategoria2 by remember { mutableStateOf("") }


            NavHost(
                navController = navController,
                startDestination = Routes.INICIAL // Define a tela inicial como ponto de partida
            ) {
                composable(Routes.INICIAL) {
                    // A tela inicial não precisa dos estados de filtro/pesquisa
                    TelaInicial(navController = navController)
                }
                composable(Routes.LISTA) {
                    // Passa os estados de filtro/pesquisa e os callbacks para a lista
                    val produtos = ProdutoRepository.getProdutos()
                    ProdutoLista(
                        produtos = produtos,
                        searchText = searchText,
                        selectedCategory = selectedCategory,
                        selectedCategoria2 = selectedCategoria2,
                        onSearchTextChanged = { searchText = it },
                        onCategorySelected = { selectedCategory = it },
                        onCategoria2Selected = { selectedCategoria2 = it },
                        navController = navController // Passa o NavController para a lista
                    )
                }
                composable(Routes.DETALHES) { backStackEntry ->
                    val codigo = backStackEntry.arguments?.getString("codigo")
                    val produto = ProdutoRepository.getProdutos().find { it.codigo == codigo }
                    if (produto != null) {
                        DetalhesProduto(produto, navController) // Passa o NavController para os detalhes
                    } else {
                        // Tela de erro ou tratamento para produto não encontrado
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