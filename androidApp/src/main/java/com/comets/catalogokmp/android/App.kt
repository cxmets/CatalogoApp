package com.comets.catalogokmp.android

import android.app.Application
import com.comets.catalogokmp.util.appContext

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}