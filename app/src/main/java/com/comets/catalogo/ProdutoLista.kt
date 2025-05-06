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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProdutoLista(
    produtos: List<Produto>,
    searchText: String,
    selectedCategory: String,
    selectedCategoria2: String,
    onSearchTextChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCategoria2Selected: (String) -> Unit,
    navController: NavController
) {
    var filtrosVisiveis by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedCategoria2 by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    val customButtonContainerColor = if (isDarkTheme) {
        Color(
            red = (backgroundColor.red + 0.15f).coerceIn(0f, 1f),
            green = (backgroundColor.green + 0.15f).coerceIn(0f, 1f),
            blue = (backgroundColor.blue + 0.15f).coerceIn(0f, 1f),
            alpha = backgroundColor.alpha
        )
    } else {
        Color(
            red = (backgroundColor.red - 0.15f).coerceIn(0f, 1f),
            green = (backgroundColor.green - 0.15f).coerceIn(0f, 1f),
            blue = (backgroundColor.blue - 0.15f).coerceIn(0f, 1f),
            alpha = backgroundColor.alpha
        )
    }

    val customButtonContentColor = onBackgroundColor

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isDarkTheme

    LaunchedEffect(systemUiController, useDarkIcons) {
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 18.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.offset(y = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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

            IconButton(
                onClick = { filtrosVisiveis = !filtrosVisiveis },
                modifier = Modifier.offset(y = 8.dp)
            ) {
                Icon(
                    imageVector = if (filtrosVisiveis) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = if (filtrosVisiveis) "Ocultar Filtros" else "Mostrar Filtros"
                )
            }
        }

        if (!filtrosVisiveis) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filtrosVisiveis) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
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
                                    expandedCategory = !expandedCategory
                                    expandedCategoria2 = false
                                }
                                .fillMaxWidth(),
                            colors = customInputColors,
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                        ) {
                            val categorias = remember(produtos) {
                                produtos.map { it.categoria }.distinct()
                            }
                            DropdownMenuItem(
                                text = { Text("Todas") },
                                onClick = {
                                    onCategorySelected("")
                                    expandedCategory = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
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
                                    expandedCategoria2 = !expandedCategoria2
                                    expandedCategory = false
                                }
                                .fillMaxWidth(),
                            colors = customInputColors,
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria2,
                            onDismissRequest = { expandedCategoria2 = false },
                        ) {
                            val filtros2 = remember(produtos) {
                                produtos.map { it.categoria2 }.distinct()
                            }
                            DropdownMenuItem(
                                text = { Text("Todas") },
                                onClick = {
                                    onCategoria2Selected("")
                                    expandedCategoria2 = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
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

                    modifier = Modifier.fillMaxWidth(0.7f)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customButtonContainerColor,
                        contentColor = customButtonContentColor
                    )
                ) {
                    Text("Limpar Filtros")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }


        val filteredProdutos = produtos.filter {
            (it.nome.contains(searchText, ignoreCase = true) || it.apelido.contains(searchText, ignoreCase = true) || it.codigo.contains(searchText, ignoreCase = true)) &&
                    (selectedCategory.isEmpty() || it.categoria == selectedCategory) &&
                    (selectedCategoria2.isEmpty() || it.categoria2 == selectedCategoria2)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProdutos) { produto ->
                ProdutoItem(produto = produto, navController = navController)
            }
        }
    }
}