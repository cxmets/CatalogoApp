package com.comets.catalogokmp.di

import com.comets.catalogokmp.util.IntentHandler
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    // Factory para o IntentHandler, que não precisa de contexto no iOS
    factory { IntentHandler() }

    // Singleton para as Settings, usando NSUserDefaults no iOS
    single<Settings> { NSUserDefaultsSettings(delegate = NSUserDefaults.standardUserDefaults) }
}