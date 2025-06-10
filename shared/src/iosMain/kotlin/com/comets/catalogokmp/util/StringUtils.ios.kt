package com.comets.catalogokmp.util

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSASCIIStringEncoding
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.decomposedStringWithCanonicalMapping
import platform.Foundation.lowercaseString

@OptIn(BetaInteropApi::class)
actual fun String.normalizeForSearch(): String {
    val nsString = NSString.create(string = this)

    val decomposed = nsString.decomposedStringWithCanonicalMapping

    val nsDecomposed = NSString.create(string = decomposed)

    val data = nsDecomposed.dataUsingEncoding(NSASCIIStringEncoding, allowLossyConversion = true)

    return if (data != null) {
        NSString.create(data = data, encoding = NSASCIIStringEncoding)
            ?.lowercaseString()
            ?.trim()
            ?: this.lowercase().trim()
    } else {
        this.lowercase().trim()
    }
}