package com.comets.catalogo

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch // Import para kotlinx.coroutines.launch

class DetalhesProdutoViewModel(
    private val codigoProduto: String?,
    private val produtoDataSource: ProdutoDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalhesProdutoUiState>(viewModelScope, DetalhesProdutoUiState.Loading)
    val uiState: StateFlow<DetalhesProdutoUiState> = _uiState.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(viewModelScope, false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    init {
        if (codigoProduto != null) {
            loadProduto(codigoProduto)
        } else {
            _uiState.value = DetalhesProdutoUiState.Error("Código do produto não fornecido")
        }
    }

    private fun loadProduto(codigo: String) {
        viewModelScope.coroutineScope.launch { // <<< CORREÇÃO AQUI
            _uiState.value = DetalhesProdutoUiState.Loading
            val result = produtoDataSource.getProdutoPorCodigo(codigo)
            result.fold(
                onSuccess = { produtoEncontrado ->
                    if (produtoEncontrado != null) {
                        _uiState.value = DetalhesProdutoUiState.Success(produtoEncontrado)
                    } else {
                        _uiState.value = DetalhesProdutoUiState.Error("Produto não encontrado")
                    }
                },
                onFailure = {
                    _uiState.value = DetalhesProdutoUiState.Error("Falha ao carregar detalhes do produto.")
                }
            )
        }
    }

    fun setIsProcessingPopBack(isProcessing: Boolean) {
        _isProcessingPopBack.value = isProcessing
    }
}