package com.comets.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions // Correct import
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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


    val keyboardController = LocalSoftwareKeyboardController.current

    // #NAVEGAÇÃO
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            exibirDetalhes = destination.route != "lista" && destination.route != null
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // Definir cores customizadas para os TextField (pesquisa e filtros)
    val isDarkTheme = isSystemInDarkTheme()
    val customInputColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,

        focusedTextColor = if (isDarkTheme) Color.White else Color.Black,
        unfocusedTextColor = if (isDarkTheme) Color.White else Color.Black,
        disabledTextColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),

        cursorColor = if (isDarkTheme) Color.White else Color.Black,

        focusedIndicatorColor = if (isDarkTheme) Color.White else Color.Black,
        unfocusedIndicatorColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
        disabledIndicatorColor = Color.Transparent,

        focusedLabelColor = if (isDarkTheme) Color.White else Color.Black,
        unfocusedLabelColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f),
        disabledLabelColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),

        focusedTrailingIconColor = if (isDarkTheme) Color.White else Color.Black,
        unfocusedTrailingIconColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f),
        disabledTrailingIconColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
    )

    Column(modifier = Modifier.fillMaxSize()) {

        if (!exibirDetalhes) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { filtrosVisiveis = !filtrosVisiveis }) {
                    Icon(
                        imageVector = if (filtrosVisiveis) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                        contentDescription = if (filtrosVisiveis) "Ocultar Filtros" else "Mostrar Filtros"
                    )
                }

                TextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    label = { Text("Pesquisar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    modifier = Modifier.weight(1f),
                    colors = customInputColors,
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                onSearchTextChanged("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar Pesquisa")
                            }
                        }
                    }
                )

                /*
                 Icon(
                     imageVector = Icons.Filled.Search,
                     contentDescription = "Ícone de Pesquisa"
                 )
                 */
            }

            // # FILTROS DROPDOWN E BOTÃO LIMPAR (Visibilidade controlada por filtrosVisiveis)
            if (filtrosVisiveis) {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // ExposedDropdownMenuBox para CATEGORIA
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                readOnly = true,
                                value = selectedCategory,
                                onValueChange = { },
                                label = { Text("Categoria") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .clickable {
                                        // --> Toggle esta categoria E feche a outra
                                        expandedCategory = !expandedCategory
                                        expandedCategoria2 = false // <-- Adicionado
                                    }
                                    .fillMaxWidth()
                                ,
                                colors = customInputColors,
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                            ) {
                                val categorias = produtos.map { it.categoria }.distinct()
                                categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria) },
                                        onClick = {
                                            onCategorySelected(categoria)
                                            expandedCategory = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // ExposedDropdownMenuBox para CATEGORIA 2
                        ExposedDropdownMenuBox(
                            expanded = expandedCategoria2,
                            onExpandedChange = { expandedCategoria2 = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                readOnly = true,
                                value = selectedCategoria2,
                                onValueChange = { },
                                label = { Text("Categoria 2") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria2) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .clickable {
                                        // --> Toggle esta categoria E feche a outra
                                        expandedCategoria2 = !expandedCategoria2
                                        expandedCategory = false // <-- Adicionado
                                    }
                                    .fillMaxWidth()
                                ,
                                colors = customInputColors,
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategoria2,
                                onDismissRequest = { expandedCategoria2 = false },
                            ) {
                                val filtros2 = produtos.map { it.categoria2 }.distinct()
                                filtros2.forEach { categoria2 ->
                                    DropdownMenuItem(
                                        text = { Text(categoria2) },
                                        onClick = {
                                            onCategoria2Selected(categoria2)
                                            expandedCategoria2 = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onSearchTextChanged("")
                            onCategorySelected("")
                            onCategoria2Selected("")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.Black else Color.White,
                            contentColor = if (isDarkTheme) Color.White else Color.Black
                        )
                    ) {
                        Text("Limpar Filtros")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("lista") {
                val filteredProdutos = produtos.filter {
                    it.nome.contains(searchText, ignoreCase = true) &&
                            (selectedCategory.isEmpty() || it.categoria == selectedCategory) &&
                            (selectedCategoria2.isEmpty() || it.categoria2 == selectedCategoria2)
                }

                if (!exibirDetalhes) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProdutos) { produto ->
                            ProdutoItem(produto = produto, navController = navController)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }
            composable("detalhes/{codigo}") { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo")
                val produto = produtos.find { it.codigo == codigo }
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