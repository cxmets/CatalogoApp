package com.comets.catalogokmp.di

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.ProdutoRepositoryImpl
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.presentation.DetalhesProdutoViewModel
import com.comets.catalogokmp.presentation.ProdutoListaViewModel
import org.koin.core.module.dsl.singleOf // Para singletons com construtores simples
import org.koin.dsl.module

val commonModule = module {
    single<ProdutoDataSource> { ProdutoRepositoryImpl() }

    // AppViewModel agora é um singleton
    singleOf(::AppViewModel) // Substitui factoryOf(::AppViewModel)

    factory { ProdutoListaViewModel(get()) }
    factory { params ->
        DetalhesProdutoViewModel(
            produtoDataSource = get(),
            codigoProduto = params.get()
        )
    }
}