package com.comets.catalogo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.comets.catalogo.platform.AssetReader

object CatalogoViewModelFactory : ViewModelProvider.Factory {

    private lateinit var application: Application
    private lateinit var produtoDataSource: ProdutoDataSource

    fun initialize(app: Application) {
        if (::application.isInitialized) return
        application = app
        val assetReader = AssetReader(application.applicationContext)
        produtoDataSource = ProdutoRepositoryImpl(assetReader)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (!::application.isInitialized || !::produtoDataSource.isInitialized) {
            throw IllegalStateException("CatalogoViewModelFactory não foi inicializada. Chame initialize(application) primeiro.")
        }

        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel() as T
            }
            modelClass.isAssignableFrom(ProdutoListaViewModel::class.java) -> {
                ProdutoListaViewModel(produtoDataSource) as T
            }
            modelClass.isAssignableFrom(DetalhesProdutoViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                val codigoProduto: String? = savedStateHandle.get("codigo")
                DetalhesProdutoViewModel(codigoProduto, produtoDataSource) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}