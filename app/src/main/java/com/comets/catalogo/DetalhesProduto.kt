package com.comets.catalogo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Import para viewModel()
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun DetalhesProduto( // REMOVIDO: produto: Produto parâmetro
    navController: NavController,
    viewModel: DetalhesProdutoViewModel = viewModel(factory = CatalogoViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val isProcessingPopBack by viewModel.isProcessingPopBack.collectAsState()
    var isImageOverlayVisible by remember { mutableStateOf(false) }

    when (val currentState = uiState) {
        is DetalhesProdutoUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DetalhesProdutoUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = currentState.message) // Exibe a mensagem de erro do ViewModel
            }
        }
        is DetalhesProdutoUiState.Success -> {
            val produto = currentState.produto // Produto obtido do estado de sucesso

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (isProcessingPopBack) {
                            return@IconButton
                        }
                        viewModel.setIsProcessingPopBack(true)

                        val popped = navController.popBackStack()
                        if (!popped) {
                            viewModel.setIsProcessingPopBack(false)
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.voltar)
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
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.LightGray)
                            .clickable(
                                enabled = !isProcessingPopBack,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (isProcessingPopBack) return@clickable
                                isImageOverlayVisible = true
                            }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/${produto.imagemUrl}")
                                .crossfade(true)
                                .build(),
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
                            .padding(16.dp)
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
                    }
                }
            }

            if (isImageOverlayVisible) {
                ZoomableImageOverlay( // ZoomableImageOverlay permanece como está
                    imageUrl = produto.imagemUrl,
                    onDismiss = {
                        isImageOverlayVisible = false
                    }
                )
            }
        }
    }
}

// A função ZoomableImageOverlay permanece inalterada (como no seu último código)
@Composable
fun ZoomableImageOverlay(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f

    BackHandler {
        if (scale > minScale) {
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
            .pointerInput(Unit) {}
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${imageUrl}")
                .build(),
            contentDescription = stringResource(id = R.string.detalhes_produto_desc_imagem_ampliada),
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
                .padding(top = 32.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.detalhes_produto_desc_fechar_zoom),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}