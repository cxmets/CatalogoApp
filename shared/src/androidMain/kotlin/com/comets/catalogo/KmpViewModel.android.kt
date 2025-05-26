package com.comets.catalogo

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel // Do AndroidX
import com.rickclephas.kmp.observableviewmodel.ViewModel // A classe base do KMP ViewModel

@Composable
actual inline fun <reified T : ViewModel> getKmpViewModel(key: String?): T { // Adicionado inline e reified
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    if (viewModelStoreOwner == null) {
        // Esta exceção é mais para garantir. Em um Composable dentro de uma Activity/Fragment,
        // LocalViewModelStoreOwner geralmente está presente.
        throw IllegalStateException("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner. Ensure this is called within a Composable that has one, typically provided by a NavHost or Activity.")
    }
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = CatalogoViewModelFactory // Usa a factory de shared/androidMain
    )
}