package com.comets.catalogokmp.di

import com.comets.catalogokmp.presentation.AppViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(commonModule, platformModule)
    }
}

class KoinDependencies : KoinComponent {
    val appViewModel: AppViewModel by inject()
}