package com.comets.catalogokmp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import catalogokmp.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.compose.LocalPlatformContext
import platform.Foundation.NSBundle

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun SharedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val platformContext = LocalPlatformContext.current

    val imageModel: Any? = remember(imageUrl) {
        if (imageUrl.startsWith("http")) {
            ImageRequest.Builder(platformContext)
                .data(imageUrl)
                .build()
        } else {
            try {
                val relativePathInBundle: String = Res.getUri("drawable/$imageUrl")
                val mainBundle = NSBundle.mainBundle
                val fullResourcePath = mainBundle.resourcePath?.let { bundlePath ->
                    "$bundlePath/$relativePathInBundle"
                }

                if (fullResourcePath != null) {
                    ImageRequest.Builder(platformContext)
                        .data(fullResourcePath)
                        .build()
                } else {
                    println("KMP/iOS: SharedAsyncImage - Não foi possível construir o caminho completo para o recurso local: '$imageUrl'. Caminho relativo: '$relativePathInBundle'")
                    null
                }
            } catch (e: Exception) {
                println("KMP/iOS: SharedAsyncImage - Erro ao resolver recurso local '$imageUrl'. Erro: ${e.message}")
                null
            }
        }
    }

    Box(
        modifier = modifier.background(Color.White)
    ) {
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                onError = { errorResult ->
                    println("KMP/iOS Coil AsyncImage Error para model '$imageModel': ${errorResult.result.throwable.message}")
                    // errorResult.result.throwable?.printStackTrace() // Para um stack trace mais completo no console do Xcode
                }
            )
        } else {
            // Fallback visual caso imageModel seja nulo (falha ao construir o path/request)
            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.3f)))
            println("KMP/iOS: SharedAsyncImage - imageModel é nulo para imageUrl: $imageUrl. Exibindo fallback.")
        }
    }
}
