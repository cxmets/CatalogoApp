// shared/src/commonMain/kotlin/com/comets/catalogokmp/util/ResourceReader.kt
package com.comets.catalogokmp.util

import catalogokmp.shared.generated.resources.Res
import org.jetbrains.compose.resources.InternalResourceApi

@OptIn(InternalResourceApi::class)
suspend fun readProductsJson(): String {
    // passa o caminho relativo dentro de composeResources/
    val bytes = Res.readBytes("files/products.json")
    return bytes.decodeToString()
}
