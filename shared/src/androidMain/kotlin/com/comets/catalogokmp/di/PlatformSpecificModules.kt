package com.comets.catalogokmp.di

import android.content.Context
import com.comets.catalogokmp.util.IntentHandler
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.Module

actual val platformModule: Module = module {
    factory { IntentHandler(androidContext()) }

    single<Settings> {
        SharedPreferencesSettings(delegate = androidContext().getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE))
    }
}