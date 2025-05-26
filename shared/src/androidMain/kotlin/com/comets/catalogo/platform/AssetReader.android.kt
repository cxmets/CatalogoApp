package com.comets.catalogo.platform

import android.content.Context
import java.io.IOException

actual class AssetReader(private val context: Context) { // Removido "actual" de "constructor"
    actual fun lerArquivoTexto(nomeArquivo: String): String {
        return try {
            context.assets.open(nomeArquivo).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw AssetReadException("Falha ao ler o asset '$nomeArquivo' no Android: ${e.message}", e)
        }
    }
}