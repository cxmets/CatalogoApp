package com.comets.catalogo.asset

import android.content.Context
import java.io.IOException

// androidMain
actual class AssetReader actual constructor(private val context: Context) { // Construtor atual que recebe Context
    actual fun lerArquivoTexto(nomeArquivo: String): String {
        return try {
            context.assets.open(nomeArquivo).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw AssetReadException("Falha ao ler o asset '$nomeArquivo' no Android: ${e.message}", e)
        }
    }
}