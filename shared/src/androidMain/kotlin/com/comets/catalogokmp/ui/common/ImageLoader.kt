package com.comets.catalogokmp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.comets.catalogokmp.util.PlatformAppContext

@Composable
internal actual fun SharedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    AsyncImage(
        model = ImageRequest.Builder(PlatformAppContext.INSTANCE) // Use o contexto injetado/global
            .data("file:///android_asset/$imageUrl")
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}