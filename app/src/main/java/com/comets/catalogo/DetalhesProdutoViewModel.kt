package com.comets.catalogo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// DetalhesProdutoUiState deve estar definido (em seu próprio arquivo ou no topo deste)
// Exemplo:
// sealed interface DetalhesProdutoUiState {
//     object Loading : DetalhesProdutoUiState
//     data class Success(val produto: Produto) : DetalhesProdutoUiState
//     data class Error(val message: String) : DetalhesProdutoUiState
// }

class DetalhesProdutoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val produtoDataSource: ProdutoDataSource // Dependência injetada
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<DetalhesProdutoUiState>(DetalhesProdutoUiState.Loading)
    val uiState: StateFlow<DetalhesProdutoUiState> = _uiState.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    init {
        val codigoProduto: String? = savedStateHandle.get<String>("codigo")
        if (codigoProduto != null) {
            loadProduto(codigoProduto)
        } else {
            _uiState.value = DetalhesProdutoUiState.Error(
                application.getString(R.string.erro_codigo_produto_nao_fornecido)
            )
        }
    }

    private fun loadProduto(codigo: String) {
        viewModelScope.launch {
            _uiState.value = DetalhesProdutoUiState.Loading
            // Usa a instância de produtoDataSource injetada
            val result: Result<Produto?> = produtoDataSource.getProdutoPorCodigo(
                getApplication<Application>().applicationContext,
                codigo
            )

            result.fold(
                onSuccess = { produtoEncontrado: Produto? ->
                    if (produtoEncontrado != null) {
                        _uiState.value = DetalhesProdutoUiState.Success(produtoEncontrado)
                    } else {
                        _uiState.value = DetalhesProdutoUiState.Error(
                            getApplication<Application>().getString(R.string.produto_nao_encontrado)
                        )
                    }
                },
                onFailure = { error ->
                    Log.e("DetalhesProdutoVM", "Erro ao carregar produto $codigo: ${error.message}", error)
                    _uiState.value = DetalhesProdutoUiState.Error(
                        getApplication<Application>().getString(R.string.erro_carregar_detalhes_produto)
                    )
                }
            )
        }
    }

    fun setIsProcessingPopBack(isProcessing: Boolean) {
        _isProcessingPopBack.value = isProcessing
    }
}