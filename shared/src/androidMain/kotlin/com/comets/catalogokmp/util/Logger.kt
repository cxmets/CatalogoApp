package com.comets.catalogokmp.util

import android.util.Log

internal actual fun printLogD(tag: String, message: String) {
    Log.d(tag, message)
}

internal actual fun printLogE(tag: String, message: String, throwable: Throwable?) {
    if (throwable != null) {
        Log.e(tag, message, throwable)
    } else {
        Log.e(tag, message)
    }
}