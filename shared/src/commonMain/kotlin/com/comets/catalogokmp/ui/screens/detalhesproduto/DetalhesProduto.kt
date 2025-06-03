package com.comets.catalogokmp.ui.screens.detalhesproduto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.detalhes_produto_config_similar_desc
import catalogokmp.shared.generated.resources.detalhes_produto_desc_fechar_zoom
import catalogokmp.shared.generated.resources.detalhes_produto_desc_imagem_ampliada
import catalogokmp.shared.generated.resources.dialog_button_cancel
import catalogokmp.shared.generated.resources.no_similar_products
import catalogokmp.shared.generated.resources.similar_criteria_confirm
import catalogokmp.shared.generated.resources.similar_criteria_select_title
import catalogokmp.shared.generated.resources.similar_products_icon_desc
import catalogokmp.shared.generated.resources.similar_products_title
import catalogokmp.shared.generated.resources.voltar
import com.comets.catalogokmp.model.SelectableSimilarityCriterion
import com.comets.catalogokmp.model.SimilarityCriterionKey
import com.comets.catalogokmp.model.UserThemePreference
import com.comets.catalogokmp.navigation.DetalhesProdutoVoyagerScreen
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.DetalhesProdutoUiState
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.ui.common.PlatformBackHandler
import com.comets.catalogokmp.ui.common.SharedAsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesProdutoScreen(
    viewModel: DetalhesProdutoViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val isProcessingPopBack by viewModel.isProcessingPopBack.collectAsState()
    val similarProdutos by viewModel.similarProdutos.collectAsState()

    val availableCriteriaForSelection by viewModel.availableCriteriaForSelection.collectAsState()
    val userSelectedSimilarityKeys by viewModel.userSelectedSimilarityCriteriaKeys.collectAsState()
    val showSimilarityCriteriaSelectionButton by viewModel.showSimilarityCriteriaSelectionButton.collectAsState()
    var showCriteriaSelectionDialog by remember { mutableStateOf(false) }

    val appViewModel: AppViewModel = koinInject()
    val userThemePreference by appViewModel.userThemePreference.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    val currentEffectiveDarkTheme = when (userThemePreference) {
        UserThemePreference.LIGHT -> false
        UserThemePreference.DARK -> true
        UserThemePreference.SYSTEM -> systemIsDark
    }

    var isImageOverlayVisible by remember { mutableStateOf(false) }
    var showSimilarProductsSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    LaunchedEffect(viewModel) {
        viewModel.setIsProcessingPopBack(false)
    }

    PlatformBackHandler(enabled = isImageOverlayVisible || showCriteriaSelectionDialog) {
        if (showCriteriaSelectionDialog) {
            showCriteriaSelectionDialog = false
        } else if (isImageOverlayVisible) {
            isImageOverlayVisible = false
        }
    }

    val lightGlowAmbient = Color.White.copy(alpha = 0.25f)
    val lightGlowSpot = Color.White.copy(alpha = 0.40f)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValuesInner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValuesInner)
                .consumeWindowInsets(paddingValuesInner)
        ) {
            when (val currentState = uiState) {
                is DetalhesProdutoUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DetalhesProdutoUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(currentState.messageResource), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        IconButton(onClick = {
                            if (isProcessingPopBack) return@IconButton
                            viewModel.setIsProcessingPopBack(true)
                            navigator.pop()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.voltar))
                        }
                    }
                }

                is DetalhesProdutoUiState.Success -> {
                    val produto = currentState.produto
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = {
                                if (isProcessingPopBack) return@IconButton
                                viewModel.setIsProcessingPopBack(true)
                                navigator.pop()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(Res.string.voltar)
                                )
                            }
                            Text(
                                text = produto.codigo,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                            if (similarProdutos.isNotEmpty() || showSimilarityCriteriaSelectionButton) {
                                IconButton(
                                    onClick = { showSimilarProductsSheet = true },
                                    enabled = !isProcessingPopBack && !showCriteriaSelectionDialog
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ViewList,
                                        contentDescription = stringResource(Res.string.similar_products_icon_desc)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(48.dp))
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        ) {
                            val imageCardBaseElevation = 2.dp
                            val imageCardDarkThemeShadowElevation = 6.dp
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                                    .then(
                                        if (currentEffectiveDarkTheme) Modifier.shadow(
                                            elevation = imageCardDarkThemeShadowElevation,
                                            shape = MaterialTheme.shapes.large,
                                            ambientColor = lightGlowAmbient,
                                            spotColor = lightGlowSpot,
                                            clip = false
                                        ) else Modifier
                                    )
                                    .clickable(
                                        enabled = !isProcessingPopBack && !showCriteriaSelectionDialog,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (!isProcessingPopBack && !showCriteriaSelectionDialog) isImageOverlayVisible = true
                                    },
                                shape = MaterialTheme.shapes.large,
                                elevation = CardDefaults.cardElevation(defaultElevation = if (currentEffectiveDarkTheme) 0.dp else imageCardBaseElevation),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentEffectiveDarkTheme) {
                                        MaterialTheme.colorScheme.surfaceContainerLowest
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                SharedAsyncImage(
                                    imageUrl = produto.imagemUrl,
                                    contentDescription = produto.nome,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.large),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val textCardBaseElevation = 1.dp
                                val textCardDarkThemeShadowElevation = 4.dp
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (currentEffectiveDarkTheme) Modifier.shadow(
                                                elevation = textCardDarkThemeShadowElevation,
                                                shape = MaterialTheme.shapes.medium,
                                                ambientColor = lightGlowAmbient,
                                                spotColor = lightGlowSpot,
                                                clip = false
                                            ) else Modifier
                                        ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (currentEffectiveDarkTheme) 0.dp else textCardBaseElevation),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (currentEffectiveDarkTheme) {
                                            MaterialTheme.colorScheme.surfaceContainerLowest
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerLowest
                                        }
                                    ),
                                    border = null
                                ) {
                                    Text(
                                        text = produto.descricao,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (currentEffectiveDarkTheme) Modifier.shadow(
                                                elevation = textCardDarkThemeShadowElevation,
                                                shape = MaterialTheme.shapes.medium,
                                                ambientColor = lightGlowAmbient,
                                                spotColor = lightGlowSpot,
                                                clip = false
                                            ) else Modifier
                                        ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (currentEffectiveDarkTheme) 0.dp else textCardBaseElevation),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (currentEffectiveDarkTheme) {
                                            MaterialTheme.colorScheme.surfaceContainerLowest
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerLowest
                                        }
                                    ),
                                    border = null
                                ) {
                                    Text(
                                        text = produto.detalhes,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 19.sp,
                                            lineHeight = 44.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (showSimilarProductsSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                if (!showCriteriaSelectionDialog) {
                                    showSimilarProductsSheet = false
                                }
                            },
                            sheetState = bottomSheetState
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(Res.string.similar_products_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showSimilarityCriteriaSelectionButton) {
                                        IconButton(
                                            onClick = {
                                                showCriteriaSelectionDialog = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Filled.Tune,
                                                contentDescription = stringResource(Res.string.detalhes_produto_config_similar_desc)
                                            )
                                        }
                                    }
                                }

                                if (similarProdutos.isEmpty()) {
                                    Text(
                                        text = stringResource(Res.string.no_similar_products),
                                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 24.dp)
                                    )
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(similarProdutos, key = { it.codigo }) { similarProduto ->
                                            ListItem(
                                                headlineContent = { Text(similarProduto.nome) },
                                                supportingContent = { Text(similarProduto.codigo) },
                                                leadingContent = {
                                                    SharedAsyncImage(
                                                        imageUrl = similarProduto.imagemUrl,
                                                        contentDescription = similarProduto.nome,
                                                        modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                },
                                                modifier = Modifier.clickable {
                                                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                                        if (!bottomSheetState.isVisible) {
                                                            showSimilarProductsSheet = false
                                                        }
                                                    }
                                                    navigator.replace(DetalhesProdutoVoyagerScreen(similarProduto.codigo))
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp))
                            }
                        }
                    }

                    if (showCriteriaSelectionDialog) {
                        SimilarityCriteriaSelectionDialog(
                            availableCriteria = availableCriteriaForSelection,
                            initiallySelectedKeys = userSelectedSimilarityKeys, // Usar o StateFlow do ViewModel
                            onDismissRequest = { showCriteriaSelectionDialog = false },
                            onConfirm = { newSelectedKeys ->
                                viewModel.updateUserSelectedSimilarityCriteria(newSelectedKeys)
                                showCriteriaSelectionDialog = false
                            }
                        )
                    }

                    if (isImageOverlayVisible) {
                        ZoomableImageOverlay(
                            imageUrl = produto.imagemUrl,
                            onDismiss = { isImageOverlayVisible = false },
                            isDarkTheme = currentEffectiveDarkTheme,
                            ambientShadowColor = lightGlowAmbient,
                            spotShadowColor = lightGlowSpot
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimilarityCriteriaSelectionDialog(
    availableCriteria: List<SelectableSimilarityCriterion>,
    initiallySelectedKeys: Set<SimilarityCriterionKey>,
    onDismissRequest: () -> Unit,
    onConfirm: (Set<SimilarityCriterionKey>) -> Unit
) {
    val selectedKeysMap = remember {
        mutableStateMapOf<SimilarityCriterionKey, Boolean>().apply {
            availableCriteria.forEach { criterion ->
                this[criterion.key] = initiallySelectedKeys.contains(criterion.key)
            }
        }
    }

    LaunchedEffect(initiallySelectedKeys, availableCriteria) {
        selectedKeysMap.clear()
        availableCriteria.forEach { criterion ->
            selectedKeysMap[criterion.key] = initiallySelectedKeys.contains(criterion.key)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(Res.string.similar_criteria_select_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                availableCriteria.forEach { criterion ->
                    if (criterion.isPresent) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedKeysMap[criterion.key] = selectedKeysMap[criterion.key] != true
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = selectedKeysMap[criterion.key] == true,
                                onCheckedChange = { isChecked ->
                                    selectedKeysMap[criterion.key] = isChecked
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(criterion.displayNameResource))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val finalSelectedKeys = selectedKeysMap.filterValues { it }.keys
                onConfirm(finalSelectedKeys)
            }) {
                Text(stringResource(Res.string.similar_criteria_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.dialog_button_cancel))
            }
        }
    )
}


@Composable
fun ZoomableImageOverlay(
    imageUrl: String,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    ambientShadowColor: Color,
    spotShadowColor: Color
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f

    PlatformBackHandler(enabled = true) {
        if (scale > minScale || offset != Offset.Zero) {
            scale = minScale
            offset = Offset.Zero
        } else {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (scale > minScale || offset != Offset.Zero) {
                        scale = minScale
                        offset = Offset.Zero
                    } else {
                        onDismiss()
                    }
                }
            )
            .pointerInput(Unit) { }
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val cardZoomBaseElevation = 4.dp
        val cardZoomDarkThemeShadowElevation = 8.dp
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .then(
                    if (isDarkTheme) Modifier.shadow(
                        elevation = cardZoomDarkThemeShadowElevation,
                        shape = MaterialTheme.shapes.medium,
                        ambientColor = ambientShadowColor,
                        spotColor = spotShadowColor,
                        clip = false
                    ) else Modifier
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, gesturePan, gestureZoom, _ ->
                        scale = (scale * gestureZoom).coerceIn(minScale, maxScale)
                        val imageDisplaySize = Size(size.width.toFloat(), size.height.toFloat())
                        val scaledWidth = imageDisplaySize.width * scale
                        val scaledHeight = imageDisplaySize.height * scale
                        val maxX = if (scaledWidth > imageDisplaySize.width) (scaledWidth - imageDisplaySize.width) / 2f else 0f
                        val maxY = if (scaledHeight > imageDisplaySize.height) (scaledHeight - imageDisplaySize.height) / 2f else 0f
                        val newOffset = offset + gesturePan
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxX, maxX),
                            y = newOffset.y.coerceIn(-maxY, maxY)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale < 1.5f) 3f else minScale
                            offset = Offset.Zero
                        },
                        onTap = { }
                    )
                }
                .graphicsLayer {
                    translationX = offset.x
                    translationY = offset.y
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                },
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else cardZoomBaseElevation),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) {
                    MaterialTheme.colorScheme.surfaceContainerLowest
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            SharedAsyncImage(
                imageUrl = imageUrl,
                contentDescription = stringResource(Res.string.detalhes_produto_desc_imagem_ampliada),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(Res.string.detalhes_produto_desc_fechar_zoom),
                tint = Color.DarkGray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}