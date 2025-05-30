package com.comets.catalogokmp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import catalogokmp.shared.generated.resources.Res
import coil3.compose.AsyncImage

@Composable
actual fun SharedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val model = if (imageUrl.startsWith("http")) {
        imageUrl
    } else {
        Res.getUri("drawable/$imageUrl")
    }

    Box(
        modifier = modifier.background(Color.White)
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}