package com.comets.catalogo.platform

expect class AssetReader {
    fun lerArquivoTexto(nomeArquivo: String): String
}

class AssetReadException(message: String, cause: Throwable? = null) : Exception(message, cause)