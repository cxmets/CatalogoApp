package com.comets.catalogokmp.util

internal expect fun printLogD(tag: String, message: String)
internal expect fun printLogE(tag: String, message: String, throwable: Throwable? = null)