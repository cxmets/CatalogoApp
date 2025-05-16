package com.comets.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource // Import para stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.comets.catalogo.ui.theme.NexpartOrangeClaro
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProdutoLista(
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
    onClearAllFiltersAndSearch: () -> Unit,
    navController: NavController,
    viewModel: ProdutoListaViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentFilteredProdutos by viewModel.filteredProdutos.collectAsState()
    val rawProdutosForDropdowns by viewModel.rawProdutosForDropdowns.collectAsState()

    val filtrosVisiveis by viewModel.filtrosVisiveis.collectAsState()
    val expandedTipo by viewModel.expandedTipo.collectAsState()
    val expandedLente by viewModel.expandedLente.collectAsState()
    val expandedHaste by viewModel.expandedHaste.collectAsState()
    val expandedRosca by viewModel.expandedRosca.collectAsState()
    val isProcessingPopBack by viewModel.isProcessingPopBack.collectAsState()

    LaunchedEffect(searchText) { viewModel.updateSearchFilter(searchText) }
    LaunchedEffect(selectedTipo) { viewModel.updateTipoFilter(selectedTipo) }
    LaunchedEffect(selectedLente) { viewModel.updateLenteFilter(selectedLente) }
    LaunchedEffect(selectedHaste) { viewModel.updateHasteFilter(selectedHaste) }
    LaunchedEffect(selectedRosca) { viewModel.updateRoscaFilter(selectedRosca) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val isDarkTheme = isSystemInDarkTheme()
    val corDestaqueUnificada = NexpartOrangeClaro

    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is ProdutoListaViewModel.UiEvent.ScrollToTop -> {
                    scope.launch {
                        gridState.scrollToItem(0)
                    }
                }
            }
        }
    }

    LaunchedEffect(searchText, selectedTipo, selectedLente, selectedHaste, selectedRosca) {
        val allFiltersAreEmptyNow = searchText.isEmpty() &&
                selectedTipo.isEmpty() &&
                selectedLente.isEmpty() &&
                selectedHaste.isEmpty() &&
                selectedRosca.isEmpty()

        if (allFiltersAreEmptyNow) {
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val cameFromDetails = previousRoute?.startsWith(Routes.DETALHES_BASE) == true
            if (!cameFromDetails) {
                viewModel.requestScrollToTop()
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
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(systemUiController, isDarkTheme) {
        systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = !isDarkTheme)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = {
                    if (isProcessingPopBack) return@IconButton
                    viewModel.setIsProcessingPopBack(true)
                    onClearAllFiltersAndSearch()
                    viewModel.closeAllDropdownsUiAction()
                    val popped = navController.popBackStack()
                    if (!popped) viewModel.setIsProcessingPopBack(false)
                },
                modifier = Modifier.offset(y = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.voltar), tint = corDestaqueUnificada)
            }

            TextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                label = { Text(stringResource(id = R.string.produto_lista_label_pesquisa)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                modifier = Modifier.weight(1f),
                colors = searchTextFieldColors,
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChanged("") }) {
                            Icon(Icons.Default.Clear, stringResource(id = R.string.produto_lista_desc_limpar_pesquisa))
                        }
                    }
                }
            )

            IconButton(
                onClick = { viewModel.toggleFiltrosVisiveis() },
                modifier = Modifier.offset(y = 8.dp)
            ) {
                Icon(
                    imageVector = if (filtrosVisiveis) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = if (filtrosVisiveis) stringResource(id = R.string.produto_lista_desc_ocultar_filtros) else stringResource(id = R.string.produto_lista_desc_mostrar_filtros),
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
                    ExposedDropdownMenuBox(expanded = expandedTipo, onExpandedChange = { viewModel.setExpandedTipo(it) }, Modifier.weight(1f)) {
                        OutlinedTextField(readOnly = true, value = selectedTipo.ifEmpty { stringResource(id = R.string.filtro_opcao_todos) }, onValueChange = {}, label = { Text(stringResource(id = R.string.filtro_label_tipo)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = { viewModel.setExpandedTipo(false) }) {
                            val tipos = remember(rawProdutosForDropdowns, selectedLente, selectedHaste, selectedRosca) {
                                rawProdutosForDropdowns.filter { p -> (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedHaste.isEmpty()||p.haste==selectedHaste) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }.map { it.tipo }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.filtro_opcao_todos_tipos)) }, onClick = { onTipoSelected(""); viewModel.setExpandedTipo(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            tipos.forEach { tipo -> DropdownMenuItem(text = { Text(tipo) }, onClick = { onTipoSelected(tipo); viewModel.setExpandedTipo(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = expandedLente, onExpandedChange = { viewModel.setExpandedLente(it) }, Modifier.weight(1f)) {
                        OutlinedTextField(readOnly = true, value = selectedLente.ifEmpty { stringResource(id = R.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(id = R.string.filtro_label_lente)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLente) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        ExposedDropdownMenu(expanded = expandedLente, onDismissRequest = { viewModel.setExpandedLente(false) }) {
                            val lentes = remember(rawProdutosForDropdowns, selectedTipo, selectedHaste, selectedRosca) {
                                rawProdutosForDropdowns.filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedHaste.isEmpty()||p.haste==selectedHaste) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }.map { it.lente }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.filtro_opcao_todas_lentes)) }, onClick = { onLenteSelected(""); viewModel.setExpandedLente(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            lentes.forEach { lente -> DropdownMenuItem(text = { Text(lente) }, onClick = { onLenteSelected(lente); viewModel.setExpandedLente(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = expandedHaste, onExpandedChange = { viewModel.setExpandedHaste(it) }, Modifier.weight(1f)) {
                        OutlinedTextField(readOnly = true, value = selectedHaste.ifEmpty { stringResource(id = R.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(id = R.string.filtro_label_haste)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHaste) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        ExposedDropdownMenu(expanded = expandedHaste, onDismissRequest = { viewModel.setExpandedHaste(false) }) {
                            val hastes = remember(rawProdutosForDropdowns, selectedTipo, selectedLente, selectedRosca) {
                                rawProdutosForDropdowns.filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedRosca.isEmpty()||p.rosca==selectedRosca) }.map { it.haste }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.filtro_opcao_todas_hastes)) }, onClick = { onHasteSelected(""); viewModel.setExpandedHaste(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            hastes.forEach { haste -> DropdownMenuItem(text = { Text(haste) }, onClick = { onHasteSelected(haste); viewModel.setExpandedHaste(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = expandedRosca, onExpandedChange = { viewModel.setExpandedRosca(it) }, Modifier.weight(1f)) {
                        OutlinedTextField(readOnly = true, value = selectedRosca.ifEmpty { stringResource(id = R.string.filtro_opcao_todas) }, onValueChange = {}, label = { Text(stringResource(id = R.string.filtro_label_rosca)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRosca) }, colors = filterOutlinedTextFieldColors, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        ExposedDropdownMenu(expanded = expandedRosca, onDismissRequest = { viewModel.setExpandedRosca(false) }) {
                            val roscas = remember(rawProdutosForDropdowns, selectedTipo, selectedLente, selectedHaste) {
                                rawProdutosForDropdowns.filter { p -> (selectedTipo.isEmpty()||p.tipo==selectedTipo) && (selectedLente.isEmpty()||p.lente==selectedLente) && (selectedHaste.isEmpty()||p.haste==selectedHaste) }.map { it.rosca }.filter { it.isNotEmpty() }.distinct().sorted()
                            }
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.filtro_opcao_todas_roscas)) }, onClick = { onRoscaSelected(""); viewModel.setExpandedRosca(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            roscas.forEach { rosca -> DropdownMenuItem(text = { Text(rosca) }, onClick = { onRoscaSelected(rosca); viewModel.setExpandedRosca(false) }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                GradientBorderLightFillButton(
                    text = stringResource(id = R.string.produto_lista_botao_limpar_filtros_busca),
                    vibrantGradient = transparentBrush,
                    lightGradient = nexpartLightGradientFillBotao,
                    textColor = Color.DarkGray,
                    onClick = {
                        onClearAllFiltersAndSearch()
                        viewModel.closeAllDropdownsUiAction()
                        keyboardController?.hide()
                        viewModel.requestScrollToTop()
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
        } else if (currentFilteredProdutos.isEmpty() && (searchText.isNotEmpty() || selectedTipo.isNotEmpty() || selectedLente.isNotEmpty() || selectedHaste.isNotEmpty() || selectedRosca.isNotEmpty())) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.produto_lista_sem_resultados_filtros))
            }
        } else if (currentFilteredProdutos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.produto_lista_sem_produtos_disponiveis))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentFilteredProdutos, key = { it.codigo }) { produto ->
                    ProdutoItem(
                        produto = produto,
                        navController = navController,
                        isNavigatingAway = isProcessingPopBack
                    )
                }
            }
        }
    }
}