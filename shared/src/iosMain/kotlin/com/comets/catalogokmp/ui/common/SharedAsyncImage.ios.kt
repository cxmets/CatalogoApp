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
import okio.Path.Companion.toPath

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
                val resourcePathString: String = Res.getUri("drawable/$imageUrl")
                resourcePathString.toPath()
            } catch (e: Exception) {
                println("KMP/iOS: Erro ao obter URI/Path para '$imageUrl'. Erro: ${e.message}")
                null
            }
        }
    }

    Box(
        modifier = modifier.background(Color.White)
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            onError = { errorResult ->
                println("KMP/iOS Coil AsyncImage Error para model '$imageModel': ${errorResult.result.throwable.message}")
            }
        )
    }
}
