package com.comets.catalogo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.comets.catalogo.ui.theme.NexpartOrangeClaro // Importando a cor diretamente
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Assumindo que Color.lighten e GradientBorderLightFillButton
// estão definidos em um arquivo comum como CommonUi.kt no mesmo pacote.

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProdutoLista(
    produtos: List<Produto>,
    searchText: String,
    selectedTipo: String,
    selectedLente: String,
    selectedHaste: String,
    selectedRosca: String,
    onSearchTextChanged: (String) -> Unit,
    onTipoSelected: (String) -> Unit,
    onLenteSelected: (String) -> Unit,
    onHasteSelected: (String) -> Unit,
    onRoscaSelected: (String) -> Unit,
    navController: NavController
) {
    var filtrosVisiveis by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedLente by remember { mutableStateOf(false) }
    var expandedHaste by remember { mutableStateOf(false) }
    var expandedRosca by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val isDarkTheme = isSystemInDarkTheme()

    val corDestaqueUnificada = NexpartOrangeClaro

    // Cores para o TextField da Pesquisa (não Outlined)
    val searchTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        cursorColor = corDestaqueUnificada,
        focusedLabelColor = corDestaqueUnificada,
        focusedIndicatorColor = corDestaqueUnificada,
        // --- MUDANÇA AQUI ---
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), // Linha visível e sutil
        // --- FIM DA MUDANÇA ---
        focusedTrailingIconColor = corDestaqueUnificada,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        selectionColors = TextSelectionColors(
            handleColor = corDestaqueUnificada,
            backgroundColor = corDestaqueUnificada.copy(alpha = 0.4f)
        ),
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor = MaterialTheme.colorScheme.error,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLabelColor = MaterialTheme.colorScheme.error,
        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // Linha sutil se desabilitado
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor = MaterialTheme.colorScheme.error
    )

    // Cores para os OutlinedTextField dos Filtros
    val filterOutlinedTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        cursorColor = corDestaqueUnificada,
        focusedLabelColor = corDestaqueUnificada,
        focusedBorderColor = corDestaqueUnificada,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        focusedTrailingIconColor = corDestaqueUnificada,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        selectionColors = TextSelectionColors(
            handleColor = corDestaqueUnificada,
            backgroundColor = corDestaqueUnificada.copy(alpha = 0.4f)
        ),
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor = MaterialTheme.colorScheme.error,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLabelColor = MaterialTheme.colorScheme.error,
        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor = MaterialTheme.colorScheme.error
    )

    // Degradê para o botão "Limpar Filtros"
    val amareloOriginalBotao = MaterialTheme.colorScheme.tertiary
    val laranjaOriginalBotao = MaterialTheme.colorScheme.primary
    val vermelhoOriginalBotao = MaterialTheme.colorScheme.secondary
    val lightenFactorBotao = 0.75f
    val amareloClarinhoBotao = amareloOriginalBotao.lighten(lightenFactorBotao, forceOpaque = true)
    val laranjaClarinhoBotao = laranjaOriginalBotao.lighten(lightenFactorBotao, forceOpaque = true)
    val vermelhoClarinhoBotao = vermelhoOriginalBotao.lighten(lightenFactorBotao, forceOpaque = true)

    val nexpartLightGradientFillBotao = Brush.horizontalGradient(
        colors = listOf(amareloClarinhoBotao, laranjaClarinhoBotao, vermelhoClarinhoBotao)
    )
    val transparentBrush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(systemUiController, isDarkTheme) { // Corrigido para usar a variável correta
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme
        )
    }

    fun closeAllDropdowns() {
        expandedTipo = false
        expandedLente = false
        expandedHaste = false
        expandedRosca = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.offset(y = 8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = corDestaqueUnificada
                )
            }

            TextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                label = { Text("Pesquisar  (Nome ou Código)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                modifier = Modifier.weight(1f),
                colors = searchTextFieldColors, // Aplicando as cores atualizadas
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChanged("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpar Pesquisa"
                            )
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
                    contentDescription = if (filtrosVisiveis) "Ocultar Filtros" else "Mostrar Filtros",
                    tint = corDestaqueUnificada
                )
            }
        }

        if (!filtrosVisiveis) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filtrosVisiveis) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Linha 1: Tipo e Lente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedTipo,
                        onExpandedChange = { closeAllDropdowns(); expandedTipo = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedTipo.ifEmpty { "Todos" },
                            onValueChange = {},
                            label = { Text("Tipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                            colors = filterOutlinedTextFieldColors,
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            val tipos = remember(produtos, selectedLente, selectedHaste, selectedRosca) {
                                produtos.filter { produto ->
                                    (selectedLente.isEmpty() || produto.lente == selectedLente) &&
                                            (selectedHaste.isEmpty() || produto.haste == selectedHaste) &&
                                            (selectedRosca.isEmpty() || produto.rosca == selectedRosca)
                                }.map { it.tipo }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text("Todos os Tipos") }, onClick = { onTipoSelected(""); expandedTipo = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            tipos.forEach { tipo -> DropdownMenuItem(text = { Text(tipo) }, onClick = { onTipoSelected(tipo); expandedTipo = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedLente,
                        onExpandedChange = { closeAllDropdowns(); expandedLente = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedLente.ifEmpty { "Todas" },
                            onValueChange = {},
                            label = { Text("Lente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLente) },
                            colors = filterOutlinedTextFieldColors,
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedLente,
                            onDismissRequest = { expandedLente = false }
                        ) {
                            val lentes = remember(produtos, selectedTipo, selectedHaste, selectedRosca) {
                                produtos.filter { produto ->
                                    (selectedTipo.isEmpty() || produto.tipo == selectedTipo) &&
                                            (selectedHaste.isEmpty() || produto.haste == selectedHaste) &&
                                            (selectedRosca.isEmpty() || produto.rosca == selectedRosca)
                                }.map { it.lente }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text("Todas as Lentes") }, onClick = { onLenteSelected(""); expandedLente = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            lentes.forEach { lente -> DropdownMenuItem(text = { Text(lente) }, onClick = { onLenteSelected(lente); expandedLente = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linha 2: Haste e Rosca
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedHaste,
                        onExpandedChange = { closeAllDropdowns(); expandedHaste = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedHaste.ifEmpty { "Todas" },
                            onValueChange = {},
                            label = { Text("Haste") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHaste) },
                            colors = filterOutlinedTextFieldColors,
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedHaste,
                            onDismissRequest = { expandedHaste = false }
                        ) {
                            val hastes = remember(produtos, selectedTipo, selectedLente, selectedRosca) {
                                produtos.filter { produto ->
                                    (selectedTipo.isEmpty() || produto.tipo == selectedTipo) &&
                                            (selectedLente.isEmpty() || produto.lente == selectedLente) &&
                                            (selectedRosca.isEmpty() || produto.rosca == selectedRosca)
                                }.map { it.haste }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text("Todas as Hastes") }, onClick = { onHasteSelected(""); expandedHaste = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            hastes.forEach { haste -> DropdownMenuItem(text = { Text(haste) }, onClick = { onHasteSelected(haste); expandedHaste = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedRosca,
                        onExpandedChange = { closeAllDropdowns(); expandedRosca = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedRosca.ifEmpty { "Todas" },
                            onValueChange = {},
                            label = { Text("Rosca") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRosca) },
                            colors = filterOutlinedTextFieldColors,
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRosca,
                            onDismissRequest = { expandedRosca = false }
                        ) {
                            val roscas = remember(produtos, selectedTipo, selectedLente, selectedHaste) {
                                produtos.filter { produto ->
                                    (selectedTipo.isEmpty() || produto.tipo == selectedTipo) &&
                                            (selectedLente.isEmpty() || produto.lente == selectedLente) &&
                                            (selectedHaste.isEmpty() || produto.haste == selectedHaste)
                                }.map { it.rosca }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text("Todas as Roscas") }, onClick = { onRoscaSelected(""); expandedRosca = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            roscas.forEach { roscaItem -> DropdownMenuItem(text = { Text(roscaItem) }, onClick = { onRoscaSelected(roscaItem); expandedRosca = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GradientBorderLightFillButton(
                    text = "Limpar Filtros e Busca",
                    vibrantGradient = transparentBrush,
                    lightGradient = nexpartLightGradientFillBotao,
                    textColor = Color.DarkGray,
                    onClick = {
                        onSearchTextChanged("")
                        onTipoSelected("")
                        onLenteSelected("")
                        onHasteSelected("")
                        onRoscaSelected("")
                        closeAllDropdowns()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.fillMaxWidth(0.7f).height(44.dp),
                    cornerRadiusDp = 21.dp,
                    borderWidth = 0.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        val filteredProdutos = remember(produtos, searchText, selectedTipo, selectedLente, selectedHaste, selectedRosca) {
            Log.d("ProdutoLista", "Calculando filteredProdutos. Produtos base: ${produtos.size}, Search: '$searchText', Tipo: '$selectedTipo', Lente: '$selectedLente', Haste: '$selectedHaste', Rosca: '$selectedRosca'")
            val result = produtos.filter { produto ->
                val searchMatch = searchText.isBlank() ||
                        produto.nome.contains(searchText, ignoreCase = true) ||
                        produto.codigo.contains(searchText, ignoreCase = true) ||
                        produto.apelido.contains(searchText, ignoreCase = true)

                val tipoMatch = selectedTipo.isEmpty() || produto.tipo.equals(selectedTipo, ignoreCase = true)
                val lenteMatch = selectedLente.isEmpty() || produto.lente.equals(selectedLente, ignoreCase = true)
                val hasteMatch = selectedHaste.isEmpty() || produto.haste.equals(selectedHaste, ignoreCase = true)
                val roscaMatch = selectedRosca.isEmpty() || produto.rosca.equals(selectedRosca, ignoreCase = true)

                searchMatch && tipoMatch && lenteMatch && hasteMatch && roscaMatch
            }
            Log.d("ProdutoLista", "filteredProdutos resultou em ${result.size} produtos.")
            if (produtos.isNotEmpty() && result.isEmpty() && searchText.isBlank() && selectedTipo.isEmpty() && selectedLente.isEmpty() && selectedHaste.isEmpty() && selectedRosca.isEmpty()){
                Log.w("ProdutoLista", "ALERTA: Produtos base não estão vazios, mas o filtro inicial resultou em zero produtos! Verifique a lógica de filtro ou os dados dos produtos.")
            }
            result
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
            contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProdutos, key = { it.codigo }) { produto ->
                ProdutoItem(produto = produto, navController = navController)
            }
        }
    }
}