package com.comets.catalogo.platform

import java.text.Normalizer

actual fun String.normalizeForSearch(): String {
    val normalizedText = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalizedText, "").lowercase().trim()
}