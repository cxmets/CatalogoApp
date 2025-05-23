package com.comets.catalogo.asset

// commonMain
expect class AssetReader {
    fun lerArquivoTexto(nomeArquivo: String): String
}

// Exceção comum para ser usada em commonMain
class AssetReadException(message: String, cause: Throwable? = null) : Exception(message, cause)