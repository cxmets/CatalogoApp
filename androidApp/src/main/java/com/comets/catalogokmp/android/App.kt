package com.comets.catalogokmp.android

import android.app.Application
import com.comets.catalogokmp.di.commonModule // Importe seu módulo Koin
import com.comets.catalogokmp.util.PlatformAppContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformAppContext.initialize(this)

        startKoin {
            // Log Koin mais detalhado (opcional, útil para debug)
            // Use androidLogger(Level.ERROR) ou Level.NONE para produção
            androidLogger(Level.DEBUG)
            // Declara o contexto Android para o Koin
            androidContext(this@App)
            // Declara seus módulos
            modules(commonModule)
        }
    }
}