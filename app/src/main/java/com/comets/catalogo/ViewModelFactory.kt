package com.comets.catalogo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

// Singleton Factory para prover ViewModels com suas dependências
object CatalogoViewModelFactory : ViewModelProvider.Factory {

    private lateinit var application: Application
    private lateinit var produtoDataSource: ProdutoDataSource

    fun initialize(application: Application) {
        this.application = application
        this.produtoDataSource = ProdutoRepositoryImpl() // Cria a instância real aqui
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (!::application.isInitialized || !::produtoDataSource.isInitialized) {
            throw IllegalStateException("CatalogoViewModelFactory não foi inicializada. Chame initialize() primeiro.")
        }

        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel() as T // AppViewModel não tem dependências por enquanto
            }
            modelClass.isAssignableFrom(ProdutoListaViewModel::class.java) -> {
                ProdutoListaViewModel(application, produtoDataSource) as T
            }
            modelClass.isAssignableFrom(DetalhesProdutoViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                DetalhesProdutoViewModel(application, savedStateHandle, produtoDataSource) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}