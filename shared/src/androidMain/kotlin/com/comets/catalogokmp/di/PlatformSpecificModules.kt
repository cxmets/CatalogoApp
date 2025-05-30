package com.comets.catalogokmp.di

import com.comets.catalogokmp.util.IntentHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.Module

actual val platformModule: Module = module {
    factory { IntentHandler(androidContext()) }
}