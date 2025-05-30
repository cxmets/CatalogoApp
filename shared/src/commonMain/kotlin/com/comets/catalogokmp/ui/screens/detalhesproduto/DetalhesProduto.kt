package com.comets.catalogokmp.ui.screens.detalhesproduto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.detalhes_produto_desc_fechar_zoom
import catalogokmp.shared.generated.resources.detalhes_produto_desc_imagem_ampliada
import catalogokmp.shared.generated.resources.voltar
import com.comets.catalogokmp.presentation.DetalhesProdutoUiState
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.ui.common.PlatformBackHandler
import com.comets.catalogokmp.ui.common.SharedAsyncImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun DetalhesProdutoScreen(
    viewModel: DetalhesProdutoViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val isProcessingPopBack by viewModel.isProcessingPopBack.collectAsState()

    var isImageOverlayVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.setIsProcessingPopBack(false)
    }

    PlatformBackHandler(enabled = isImageOverlayVisible) {
        isImageOverlayVisible = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValuesInner ->
        Box(modifier = Modifier
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
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(48.dp))
                        }

                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(
                                        enabled = !isProcessingPopBack,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (!isProcessingPopBack) isImageOverlayVisible = true
                                    }
                            ) {
                                SharedAsyncImage(
                                    imageUrl = produto.imagemUrl,
                                    contentDescription = produto.nome,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = produto.nome,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(
                                    text = produto.descricao,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = produto.detalhes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    if (isImageOverlayVisible) {
                        ZoomableImageOverlay(
                            imageUrl = produto.imagemUrl,
                            onDismiss = { isImageOverlayVisible = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomableImageOverlay(
    imageUrl: String,
    onDismiss: () -> Unit
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
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .pointerInput(Unit) { }
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        SharedAsyncImage(
            imageUrl = imageUrl,
            contentDescription = stringResource(Res.string.detalhes_produto_desc_imagem_ampliada),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
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
                        onTap = {
                        }
                    )
                }
                .graphicsLayer {
                    translationX = offset.x
                    translationY = offset.y
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                },
            contentScale = ContentScale.Fit
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
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