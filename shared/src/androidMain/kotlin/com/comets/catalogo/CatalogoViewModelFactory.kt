package com.comets.catalogoappkmp.android // Ou o pacote do seu androidApp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
// Importe suas classes do módulo :shared. O Android Studio pode precisar de ajuda aqui.
// O nome do pacote no shared para as classes é com.comets.catalogo
import com.comets.catalogo.AppViewModel
import com.comets.catalogo.DetalhesProdutoViewModel
import com.comets.catalogo.ProdutoDataSource
import com.comets.catalogo.ProdutoListaViewModel
import com.comets.catalogo.ProdutoRepositoryImpl
import com.comets.catalogo.asset.AssetReader // Importa o 'actual' AssetReader do androidMain via :shared

object CatalogoViewModelFactory : ViewModelProvider.Factory {

    private lateinit var application: Application
    private lateinit var produtoDataSource: ProdutoDataSource

    // Este método precisa ser chamado pela sua classe Application do androidApp ou na MainActivity
    fun initialize(app: Application) {
        if (::application.isInitialized) return // Evita reinicialização
        application = app
        val assetReader = AssetReader(application.applicationContext) // Cria o AssetReader do androidMain
        produtoDataSource = ProdutoRepositoryImpl(assetReader)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (!::application.isInitialized || !::produtoDataSource.isInitialized) {
            throw IllegalStateException("CatalogoViewModelFactory não foi inicializada. Chame initialize(application) primeiro, por exemplo, na sua MainActivity ou Application class.")
        }

        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel() as T
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