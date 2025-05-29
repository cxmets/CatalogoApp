package com.comets.catalogokmp.util

import org.jetbrains.compose.resources.resource

internal actual suspend fun readProductsJson(): String {
    return try {
        val resource = resource("files/products.json")
        val byteArray = resource.readBytes()
        byteArray.decodeToString()
    } catch (e: Exception) {
        printLogE("ResourceReader", "Erro lendo products.json de KMP resources (androidMain): ${e.message}", e)
        throw e
    }
}