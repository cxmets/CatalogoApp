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
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.detalhes_produto_desc_fechar_zoom
import catalogokmp.shared.generated.resources.detalhes_produto_desc_imagem_ampliada
import catalogokmp.shared.generated.resources.voltar
import com.comets.catalogokmp.presentation.DetalhesProdutoUiState
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.ui.common.SharedAsyncImage
import org.jetbrains.compose.resources.stringResource


@Composable
fun DetalhesProdutoScreen(
    viewModel: DetalhesProdutoViewModel,
    onNavigateBack: () -> Unit,
    onImageDismiss: () -> Unit,
    isImageOverlayVisible: Boolean,
    onImageZoomRequested: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isProcessingPopBack by viewModel.isProcessingPopBack.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0,0,0,0) // Para o Scaffold não aplicar seus próprios insets
    ) { paddingValuesInner ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValuesInner) // Aplica o padding do Scaffold (nenhum neste caso devido ao contentWindowInsets)
            .consumeWindowInsets(paddingValuesInner) // Consome qualquer padding aplicado pelo Scaffold
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
                            .windowInsetsPadding(WindowInsets.systemBars) // Padding para status e nav bar
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = currentState.message, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        IconButton(onClick = onNavigateBack) {
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
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)) // Padding para Status Bar
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                if (isProcessingPopBack) {
                                    return@IconButton
                                }
                                viewModel.setIsProcessingPopBack(true)
                                onNavigateBack()
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
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)) // Padding para nav bar
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
                                        if (!isProcessingPopBack) onImageZoomRequested()
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
                            onDismiss = onImageDismiss,
                            onActualDismiss = onImageDismiss // Chamado pelo BackHandler interno do overlay se necessário
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
    onDismiss: () -> Unit,
    onActualDismiss: () -> Unit // Este é para o caso do BackHandler interno do overlay
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f

    // O BackHandler para o overlay deve ser tratado pela tela que o chama (DetalhesProdutoScreen)
    // para integrar com o sistema de navegação da plataforma.
    // No entanto, se o overlay tem sua própria lógica de "voltar" (como resetar zoom antes de fechar),
    // ela pode ser mantida aqui e chamar onActualDismiss quando o overlay realmente deve fechar.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss // Clicar fora da imagem (no fundo escuro) fecha
            )
            .pointerInput(Unit) { /* Consome toques */ }
            .windowInsetsPadding(WindowInsets.systemBars) // Para o overlay ocupar toda a tela e respeitar insets
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
                            // Não fazer nada no tap da imagem, o clique no Box externo fecha
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
            onClick = onDismiss, // Botão X sempre chama o onDismiss passado
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp) // Padding geral para o botão
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(Res.string.detalhes_produto_desc_fechar_zoom),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}