package com.comets.catalogokmp.util

import android.content.Context

internal lateinit var appContext: Context

internal actual fun readProductsJson(): String {
    return appContext.assets.open("products.json").bufferedReader().use { it.readText() }
}