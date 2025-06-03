package com.comets.catalogokmp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import catalogokmp.shared.generated.resources.Res
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

@Composable
actual fun SharedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    if (imageUrl.startsWith("http")) {
        val imageBitmap: ImageBitmap? by produceState<ImageBitmap?>(initialValue = null, imageUrl) {
            value = withContext(Dispatchers.IO) { // Use Dispatchers.IO for network operations
                try {
                    val httpClient = HttpClient(Darwin) // Darwin engine for iOS
                    val imageBytes = httpClient.get(imageUrl).body<ByteArray>()
                    httpClient.close()
                    Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
                } catch (e: Exception) {
                    println("Erro ao carregar imagem da URL: $imageUrl - $e")
                    null
                }
            }
        }

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
            Box(modifier = modifier.background(Color.Gray)) // Placeholder
        }
    } else {
        // Carregar de composeResources
        try {
            val drawableResource = Res.drawable.get(imageUrl) // Tenta obter o DrawableResource
            Image(
                painter = org.jetbrains.compose.resources.painterResource(drawableResource),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        } catch (e: Exception) {
            println("Erro ao carregar imagem dos resources: $imageUrl - $e")
            Box(modifier = modifier.background(Color.LightGray)) // Placeholder para resource não encontrado
        }
    }
}