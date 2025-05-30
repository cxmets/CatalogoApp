package com.comets.catalogokmp.ui.screens.listaprodutos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.filtro_label_haste
import catalogokmp.shared.generated.resources.filtro_label_lente
import catalogokmp.shared.generated.resources.filtro_label_rosca
import catalogokmp.shared.generated.resources.filtro_label_tipo
import catalogokmp.shared.generated.resources.filtro_opcao_todas
import catalogokmp.shared.generated.resources.filtro_opcao_todas_hastes
import catalogokmp.shared.generated.resources.filtro_opcao_todas_lentes
import catalogokmp.shared.generated.resources.filtro_opcao_todas_roscas
import catalogokmp.shared.generated.resources.filtro_opcao_todos
import catalogokmp.shared.generated.resources.filtro_opcao_todos_tipos
import catalogokmp.shared.generated.resources.produto_lista_botao_limpar_filtros_busca
import catalogokmp.shared.generated.resources.produto_lista_desc_limpar_pesquisa
import catalogokmp.shared.generated.resources.produto_lista_desc_mostrar_filtros
import catalogokmp.shared.generated.resources.produto_lista_desc_ocultar_filtros
import catalogokmp.shared.generated.resources.produto_lista_label_pesquisa
import catalogokmp.shared.generated.resources.produto_lista_sem_produtos_disponiveis
import catalogokmp.shared.generated.resources.produto_lista_sem_resultados_filtros
import catalogokmp.shared.generated.resources.voltar
import com.comets.catalogokmp.navigation.DetalhesProdutoVoyagerScreen
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.ProdutoListaViewModel
import com.comets.catalogokmp.ui.common.GradientBorderLightFillButton
import com.comets.catalogokmp.ui.common.lighten
import com.comets.catalogokmp.ui.components.ProdutoItem
import com.comets.catalogokmp.ui.theme.NexpartOrangeClaro
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoListaScreen(
    appViewModel: AppViewModel,
    produtoListaViewModel: ProdutoListaViewModel
) {
    val navigator = LocalNavigator.currentOrThrow

    val isLoading by produtoListaViewModel.isLoading.collectAsState()
    val loadErrorMessage by produtoListaViewModel.loadError.collectAsState()
    val currentFilteredProdutos by produtoListaViewModel.filteredProdutos.collectAsState()
    val rawProdutosForDropdowns by produtoListaViewModel.rawProdutosForDropdowns.collectAsState()

    val filtrosVisiveis by produtoListaViewModel.filtrosVisiveis.collectAsState()
    val expandedTipo by produtoListaViewModel.expandedTipo.collectAsState()
    val expandedLente by produtoListaViewModel.expandedLente.collectAsState()
    val expandedHaste by produtoListaViewModel.expandedHaste.collectAsState()
    val expandedRosca by produtoListaViewModel.expandedRosca.collectAsState()
    val isProcessingPopBack by produtoListaViewModel.isProcessingPopBack.collectAsState()

    val searchText by appViewModel.searchText.collectAsState()
    val selectedTipo by appViewModel.selectedTipo.collectAsState()
    val selectedLente by appViewModel.selectedLente.collectAsState()
    val selectedHaste by appViewModel.selectedHaste.collectAsState()
    val selectedRosca by appViewModel.selectedRosca.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val corDestaqueUnificada = NexpartOrangeClaro
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(searchText) { produtoListaViewModel.updateSearchFilter(searchText) }
    LaunchedEffect(selectedTipo) { produtoListaViewModel.updateTipoFilter(selectedTipo) }
    LaunchedEffect(selectedLente) { produtoListaViewModel.updateLenteFilter(selectedLente) }
    LaunchedEffect(selectedHaste) { produtoListaViewModel.updateHasteFilter(selectedHaste) }
    LaunchedEffect(selectedRosca) { produtoListaViewModel.updateRoscaFilter(selectedRosca) }

    LaunchedEffect(Unit) {
        produtoListaViewModel.uiEvents.collectLatest { event ->
            when (event) {
                is ProdutoListaViewModel.UiEvent.ScrollToTop -> {
                    scope.launch {
                        gridState.scrollToItem(0)
                    }
                }
            }
        }
    }

    val searchTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent,
        cursorColor = corDestaqueUnificada, focusedLabelColor = corDestaqueUnificada,
        focusedIndicatorColor = corDestaqueUnificada,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        focusedTrailingIconColor = corDestaqueUnificada,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        selectionColors = TextSelectionColors(handleColor = corDestaqueUnificada, backgroundColor = corDestaqueUnificada.copy(alpha = 0.4f)),
    )
    val filterOutlinedTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent,
        cursorColor = corDestaqueUnificada, focusedLabelColor = corDestaqueUnificada,
        focusedBorderColor = corDestaqueUnificada,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        focusedTrailingIconColor = corDestaqueUnificada,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        selectionColors = TextSelectionColors(handleColor = corDestaqueUnificada, backgroundColor = corDestaqueUnificada.copy(alpha = 0.4f)),
    )
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        if (isProcessingPopBack) return@IconButton
                        produtoListaViewModel.setIsProcessingPopBack(true)
                        appViewModel.clearAllFilters()
                        produtoListaViewModel.requestScrollToTop()
                        produtoListaViewModel.closeAllDropdownsUiAction()
                        navigator.pop()
                    },
                    modifier = Modifier.offset(y = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.voltar), tint = corDestaqueUnificada)
                }

                TextField(
                    value = searchText,
                    onValueChange = { appViewModel.onSearchTextChanged(it) },
                    label = { Text(stringResource(Res.string.produto_lista_label_pesquisa)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                    modifier = Modifier.weight(1f),
                    colors = searchTextFieldColors,
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { appViewModel.onSearchTextChanged("") }) {
                                Icon(Icons.Default.Clear, stringResource(Res.string.produto_lista_desc_limpar_pesquisa))
                            }
                        }
                    }
                )

                IconButton(
                    onClick = { produtoListaViewModel.toggleFiltrosVisiveis() },
                    modifier = Modifier.offset(y = 8.dp)
                ) {
                    Icon(
                        imageVector = if (filtrosVisiveis) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                        contentDescription = if (filtrosVisiveis) stringResource(Res.string.produto_lista_desc_ocultar_filtros) else stringResource(Res.string.produto_lista_desc_mostrar_filtros),
                        tint = corDestaqueUnificada
                    )
                }
            }

            if (!filtrosVisiveis) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filtrosVisiveis) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(expanded = expandedTipo, onExpandedChange = { produtoListaViewModel.setExpandedTipo(it) }, Modifier.weight(1f)) {
                            OutlinedTextField(readOnly = true, value = selectedTipo.ifEmpty { stringResource(Res.string.filtro_opcao_todos) }, onValueChange = {}, label = { Text(stringResource(Res.string.filtro_label_tipo)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                            ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = { produtoListaViewModel.setExpandedTipo(false) }) {
                                val tipos = remember(rawProdutosForDropdowns, selectedLente, selectedHaste, selectedRosca) {
                                    rawProdutosForDropdowns.asSequence().filter { p -> (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedHaste.isEmpty()||p.haste==selectedHaste) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }
                                        .map { it.tipo }.filter { it.isNotEmpty() }.distinct().sorted()
                                        .toList()
                                }
                                DropdownMenuItem(text = { Text(stringResource(Res.string.filtro_opcao_todos_tipos)) }, onClick = { appViewModel.onTipoSelected(""); produtoListaViewModel.setExpandedTipo(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                                tipos.forEach { tipo -> DropdownMenuItem(text = { Text(tipo) }, onClick = { appViewModel.onTipoSelected(tipo); produtoListaViewModel.setExpandedTipo(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                            }
                        }
                        ExposedDropdownMenuBox(expanded = expandedLente, onExpandedChange = { produtoListaViewModel.setExpandedLente(it) }, Modifier.weight(1f)) {
                            OutlinedTextField(readOnly = true, value = selectedLente.ifEmpty { stringResource(Res.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(Res.string.filtro_label_lente)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLente) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                            ExposedDropdownMenu(expanded = expandedLente, onDismissRequest = { produtoListaViewModel.setExpandedLente(false) }) {
                                val lentes = remember(rawProdutosForDropdowns, selectedTipo, selectedHaste, selectedRosca) {
                                    rawProdutosForDropdowns.asSequence().filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedHaste.isEmpty()||p.haste==selectedHaste) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }
                                        .map { it.lente }.filter { it.isNotEmpty() }.distinct().sorted()
                                        .toList()
                                }
                                DropdownMenuItem(text = { Text(stringResource(Res.string.filtro_opcao_todas_lentes)) }, onClick = { appViewModel.onLenteSelected(""); produtoListaViewModel.setExpandedLente(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                                lentes.forEach { lente -> DropdownMenuItem(text = { Text(lente) }, onClick = { appViewModel.onLenteSelected(lente); produtoListaViewModel.setExpandedLente(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(expanded = expandedHaste, onExpandedChange = { produtoListaViewModel.setExpandedHaste(it) }, Modifier.weight(1f)) {
                            OutlinedTextField(readOnly = true, value = selectedHaste.ifEmpty { stringResource(Res.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(Res.string.filtro_label_haste)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHaste) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                            ExposedDropdownMenu(expanded = expandedHaste, onDismissRequest = { produtoListaViewModel.setExpandedHaste(false) }) {
                                val hastes = remember(rawProdutosForDropdowns, selectedTipo, selectedLente, selectedRosca) {
                                    rawProdutosForDropdowns.asSequence().filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }
                                        .map { it.haste }.filter { it.isNotEmpty() }.distinct().sorted()
                                        .toList()
                                }
                                DropdownMenuItem(text = { Text(stringResource(Res.string.filtro_opcao_todas_hastes)) }, onClick = { appViewModel.onHasteSelected(""); produtoListaViewModel.setExpandedHaste(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                                hastes.forEach { haste -> DropdownMenuItem(text = { Text(haste) }, onClick = { appViewModel.onHasteSelected(haste); produtoListaViewModel.setExpandedHaste(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                            }
                        }
                        ExposedDropdownMenuBox(expanded = expandedRosca, onExpandedChange = { produtoListaViewModel.setExpandedRosca(it) }, Modifier.weight(1f)) {
                            OutlinedTextField(readOnly = true, value = selectedRosca.ifEmpty { stringResource(Res.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(Res.string.filtro_label_rosca)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRosca) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                            ExposedDropdownMenu(expanded = expandedRosca, onDismissRequest = { produtoListaViewModel.setExpandedRosca(false) }) {
                                val roscas = remember(rawProdutosForDropdowns, selectedTipo, selectedLente, selectedHaste) {
                                    rawProdutosForDropdowns.asSequence().filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedHaste.isEmpty()||p.haste==selectedHaste) }
                                        .map { it.rosca }.filter { it.isNotEmpty() }.distinct().sorted()
                                        .toList()
                                }
                                DropdownMenuItem(text = { Text(stringResource(Res.string.filtro_opcao_todas_roscas)) }, onClick = { appViewModel.onRoscaSelected(""); produtoListaViewModel.setExpandedRosca(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                                roscas.forEach { rosca -> DropdownMenuItem(text = { Text(rosca) }, onClick = { appViewModel.onRoscaSelected(rosca); produtoListaViewModel.setExpandedRosca(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    GradientBorderLightFillButton(
                        text = stringResource(Res.string.produto_lista_botao_limpar_filtros_busca),
                        vibrantGradient = transparentBrush,
                        lightGradient = nexpartLightGradientFillBotao,
                        textColor = Color.DarkGray,
                        onClick = {
                            appViewModel.clearAllFilters()
                            produtoListaViewModel.closeAllDropdownsUiAction()
                            keyboardController?.hide()
                            produtoListaViewModel.requestScrollToTop()
                        },
                        modifier = Modifier.fillMaxWidth(0.7f).height(44.dp),
                        cornerRadiusDp = 21.dp,
                        borderWidth = 0.dp
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (loadErrorMessage != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = loadErrorMessage!!)
                }
            } else {
                if (rawProdutosForDropdowns.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.produto_lista_sem_produtos_disponiveis))
                    }
                } else if (currentFilteredProdutos.isEmpty()) {
                    val isAnyFilterActive = searchText.isNotEmpty() ||
                            selectedTipo.isNotEmpty() ||
                            selectedLente.isNotEmpty() ||
                            selectedHaste.isNotEmpty() ||
                            selectedRosca.isNotEmpty()
                    if (isAnyFilterActive) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(Res.string.produto_lista_sem_resultados_filtros))
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {}
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentFilteredProdutos, key = { it.codigo }) { produto ->
                            ProdutoItem(
                                produto = produto,
                                onItemClick = { produtoId ->
                                    navigator.push(DetalhesProdutoVoyagerScreen(produtoId))
                                },
                                isNavigatingAway = isProcessingPopBack
                            )
                        }
                    }
                }
            }
        }
    }
}