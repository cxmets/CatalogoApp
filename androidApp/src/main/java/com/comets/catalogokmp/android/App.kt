package com.comets.catalogokmp.android

import android.app.Application
import com.comets.catalogokmp.util.PlatformAppContext

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformAppContext.initialize(this)
    }
}