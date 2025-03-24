package com.comets.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoLista(
    produtos: List<Produto>,
    searchText: String,
    selectedCategory: String,
    selectedCategoria2: String,
    onSearchTextChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCategoria2Selected: (String) -> Unit
) {
    val navController = rememberNavController()
    var exibirDetalhes by remember { mutableStateOf(false) }
    var filtrosVisiveis by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedCategoria2 by remember { mutableStateOf(false) }

    // #NAVEGAÇÃO
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            exibirDetalhes = destination.route != "lista"
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // #FILTRO
    Column(modifier = Modifier.padding(3.dp)) {
        if (!exibirDetalhes) {
            if (filtrosVisiveis) {
                TextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    label = { Text("Pesquisar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        TextField(
                            readOnly = true,
                            value = selectedCategory,
                            onValueChange = { },
                            label = { Text("Categoria") },
                            trailingIcon = {
                                IconButton(onClick = { expandedCategory = !expandedCategory }) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                }
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCategory = !expandedCategory }
                        )
                        DropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categorias = produtos.map { it.categoria }.distinct()
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria) },
                                    onClick = {
                                        onCategorySelected(categoria)
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        TextField(
                            readOnly = true,
                            value = selectedCategoria2,
                            onValueChange = { },
                            label = { Text("Categoria 2") },
                            trailingIcon = {
                                IconButton(onClick = { expandedCategoria2 = !expandedCategoria2 }) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria2)
                                }
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCategoria2 = !expandedCategoria2 }
                        )
                        DropdownMenu(
                            expanded = expandedCategoria2,
                            onDismissRequest = { expandedCategoria2 = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val filtros2 = produtos.map { it.categoria2 }.distinct()
                            filtros2.forEach { categoria2 ->
                                DropdownMenuItem(
                                    text = { Text(categoria2) },
                                    onClick = {
                                        onCategoria2Selected(categoria2)
                                        expandedCategoria2 = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    onSearchTextChanged("")
                    onCategorySelected("")
                    onCategoria2Selected("")
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Limpar Filtros")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (exibirDetalhes) {
                    // Botão de voltar removido daqui, pois já está em DetalhesProduto.kt
                    Spacer(modifier = Modifier.width(48.dp)) // Espaço para alinhar o botão de filtro
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                IconButton(onClick = { filtrosVisiveis = !filtrosVisiveis }) {
                    Icon(
                        imageVector = if (filtrosVisiveis) Icons.Filled.KeyboardArrowUp else Icons.Filled.Search,
                        contentDescription = if (filtrosVisiveis) "Ocultar Filtros" else "Filtrar"
                    )
                }
            }
        }

        // #LISTAGEM PRODUTOS
        NavHost(navController = navController, startDestination = "lista") {
            composable("lista") {
                val filteredProdutos = produtos.filter {
                    it.nome.contains(searchText, ignoreCase = true) &&
                            (selectedCategory.isEmpty() || it.categoria == selectedCategory) &&
                            (selectedCategoria2.isEmpty() || it.categoria2 == selectedCategoria2)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProdutos) { produto ->
                        ProdutoItem(produto = produto, navController = navController)
                    }
                }
            }
            composable("detalhes/{nome}") { backStackEntry ->
                val nome = backStackEntry.arguments?.getString("nome")
                val produto = produtos.find { it.nome == nome }
                if (produto != null) {
                    DetalhesProduto(produto, navController)
                }
            }
        }
    }
}