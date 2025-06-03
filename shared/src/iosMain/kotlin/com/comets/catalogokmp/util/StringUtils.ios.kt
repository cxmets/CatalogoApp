package com.comets.catalogokmp.util

import platform.Foundation.NSString
import platform.Foundation.stringByFoldingWithOptions
import platform.Foundation.NSDiacriticInsensitiveSearch
import platform.Foundation.NSCaseInsensitiveSearch
import platform.Foundation.NSWidthInsensitiveSearch

actual fun String.normalizeForSearch(): String {
    val nsString = this as NSString
    return nsString.stringByFoldingWithOptions(
        options = NSDiacriticInsensitiveSearch or NSCaseInsensitiveSearch or NSWidthInsensitiveSearch,
        locale = null
    ).trim()
}