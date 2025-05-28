package com.comets.catalogokmp.util

import java.text.Normalizer

fun String.normalizeForSearch(): String {
    val normalizedText = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalizedText, "").lowercase().trim()
}