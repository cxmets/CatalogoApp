package com.comets.catalogokmp.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(commonModule, platformModule)
    }
}