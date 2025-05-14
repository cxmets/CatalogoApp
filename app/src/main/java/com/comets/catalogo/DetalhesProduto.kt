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
import androidx.compose.material.icons.filled.Close // Import para o ícone 'X'
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun DetalhesProduto(produto: Produto, navController: NavController) {
    var isImageOverlayVisible by remember { mutableStateOf(false) }
    var isProcessingPopBack by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp) // Ajuste conforme o padding do topo da sua ProdutoLista
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
                isProcessingPopBack = true

                val popped = navController.popBackStack()
                if (!popped) {
                    isProcessingPopBack = false
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
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

            // Coluna para os Textos de Detalhes do Produto com espaçamento ajustado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(10.dp)) // Espaço antes do nome

                Text( // NOME
                    text = produto.nome,
                    style = MaterialTheme.typography.headlineSmall, // Usando um estilo de tema para o nome
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // AFASTAR descrição do nome
                Spacer(modifier = Modifier.height(28.dp)) // Antes era 8.dp

                Text( // DESCRIÇÃO
                    text = produto.descricao,
                    style = MaterialTheme.typography.bodyLarge, // Usando um estilo de tema para a descrição
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // APROXIMAR descrição dos detalhes
                Spacer(modifier = Modifier.height(8.dp)) // Antes era 16.dp

                Text( // DETALHES
                    text = produto.detalhes,
                    style = MaterialTheme.typography.bodyMedium, // Usando um estilo de tema para os detalhes
                    modifier = Modifier.fillMaxWidth(), // Para ocupar a largura e permitir alinhamento se necessário
                    textAlign = TextAlign.Start // Alinhar detalhes à esquerda para melhor leitura
                )
            }
        }
    }

    if (isImageOverlayVisible) {
        ZoomableImageOverlay(
            imageUrl = produto.imagemUrl,
            onDismiss = {
                isImageOverlayVisible = false
            }
        )
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
            .clickable( // Permite fechar clicando no fundo
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .pointerInput(Unit) {} // Consome toques para não vazar para a tela de baixo
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/${imageUrl}")
                .build(),
            contentDescription = "Imagem Ampliada",
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
                        // Não definir onClick aqui para que o clique no fundo do Box funcione para dispensar
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

        // Botão 'X' para fechar
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd) // Alinha no canto superior direito
                .padding(top = 32.dp, end = 23.dp) // Padding para afastar das bordas
        ) {
            Icon(
                imageVector = Icons.Filled.Close, // Ícone 'X'
                contentDescription = "Fechar zoom da imagem",
                tint = Color.LightGray, // Cor branca para bom contraste com o fundo escuro
                modifier = Modifier.size(40.dp) // Tamanho do ícone
            )
        }
    }
}