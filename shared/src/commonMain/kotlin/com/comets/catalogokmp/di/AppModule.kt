package com.comets.catalogokmp.di

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.ProdutoRepositoryImpl
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.presentation.ProdutoListaViewModel
import org.koin.dsl.module

val commonModule = module {
    single<ProdutoDataSource> { ProdutoRepositoryImpl() }

    single { AppViewModel(produtoDataSource = get(), settings = get()) }

    factory { ProdutoListaViewModel(produtoDataSource = get(), appViewModel = get()) }
    factory { params ->
        DetalhesProdutoViewModel(
            produtoDataSource = get(),
            currentCodigoProduto = params.get(),
            appViewModel = get()
        )
    }
}
