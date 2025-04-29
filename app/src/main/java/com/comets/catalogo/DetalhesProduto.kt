package com.comets.catalogo

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign // <-- Importar TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesProduto(produto: Produto, navController: NavController) {
    val context = LocalContext.current
    val assetManager = context.assets

    var isImageOverlayVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = produto.codigo,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Placeholder invisível (lado direito)
                    IconButton(
                        onClick = { /* Nada acontece ao clicar */ },
                        enabled = false
                    ) {
                        // Sem conteúdo visual
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight() // Mantido conforme sua preferência
                    .background(Color.LightGray)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isImageOverlayVisible = true
                    }
            ) {
                val inputStream = assetManager.open(produto.imagemUrl)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val imageBitmap = bitmap.asImageBitmap()

                Image(
                    bitmap = imageBitmap,
                    contentDescription = produto.nome,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth() // Garante que a coluna use toda a largura
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = produto.nome,
                    fontSize = 24.sp,
                    // --> Remover align, adicionar fillMaxWidth e textAlign
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = produto.descricao,
                    fontSize = 16.sp,
                    // --> Remover align, adicionar fillMaxWidth e textAlign
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                // O texto de detalhes permanece alinhado à esquerda
                Text(text = produto.detalhes, fontSize = 16.sp)
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

// O Composable ZoomableImageOverlay permanece o mesmo
// ... (código de ZoomableImageOverlay) ...

@Composable
fun ZoomableImageOverlay(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val assetManager = context.assets

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val maxScale = 5f
    val minScale = 1f

    BackHandler {
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable {
                onDismiss()
            }
            .pointerInput(Unit) {}
    ) {
        val inputStream = assetManager.open(imageUrl)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        val imageBitmap = bitmap.asImageBitmap()

        Image(
            bitmap = imageBitmap,
            contentDescription = "Zoomable image",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTransformGestures { gestureCentroid, gesturePan, gestureZoom, _ ->
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
                        onDoubleTap = { tapOffset ->
                            scale = if (scale > minScale) minScale else maxScale
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
    }
}