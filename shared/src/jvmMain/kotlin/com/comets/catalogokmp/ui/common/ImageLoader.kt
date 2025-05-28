package com.comets.catalogokmp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource // Para carregar de composeResources

@Composable
internal actual fun SharedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    // Para JVM (Desktop), painterResource funciona se a imagem estiver em composeResources/drawable
    // A string imageUrl aqui seria o nome do arquivo, ex: "nome_da_imagem.png"
    // Se o products.json tiver caminhos como "pasta/imagem.png", ajuste aqui.
    // Por simplicidade, estamos assumindo que imageUrl é apenas o nome do arquivo.
    // Para carregar do sistema de arquivos local na JVM, seria necessário uma lógica diferente.
    try {
        Image(
            painter = painterResource(imageUrl), // Tenta carregar de composeResources
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } catch (e: Exception) {
        // Fallback ou log de erro se a imagem não puder ser carregada
        // Poderia exibir um placeholder ou nada
        System.err.println("Erro ao carregar imagem na JVM com painterResource ('$imageUrl'): ${e.message}")
        // androidx.compose.foundation.layout.Box(modifier.background(androidx.compose.ui.graphics.Color.Gray)) {}
    }
}