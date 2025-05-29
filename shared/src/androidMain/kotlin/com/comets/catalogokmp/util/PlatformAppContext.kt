package com.comets.catalogokmp.util

import android.content.Context

object PlatformAppContext {
    lateinit var INSTANCE: Context
        private set

    fun initialize(context: Context) {
        INSTANCE = context.applicationContext
    }
}