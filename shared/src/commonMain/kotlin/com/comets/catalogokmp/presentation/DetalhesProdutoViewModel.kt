package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.model.Produto
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalhesProdutoViewModel(
    private val produtoDataSource: ProdutoDataSource,
    private val codigoProduto: String? // Recebe o código diretamente
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalhesProdutoUiState>(DetalhesProdutoUiState.Loading)
    val uiState: StateFlow<DetalhesProdutoUiState> = _uiState.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    init {
        if (codigoProduto != null) {
            loadProduto(codigoProduto)
        } else {
            // Idealmente, teríamos acesso a strings de recursos de forma KMP.
            // Por ora, uma string literal ou uma expect/actual para strings.
            _uiState.value = DetalhesProdutoUiState.Error("Código do produto não fornecido.")
        }
    }

    private fun loadProduto(codigo: String) {
        viewModelScope.launch {
            _uiState.value = DetalhesProdutoUiState.Loading
            val result: Result<Produto?> = produtoDataSource.getProdutoPorCodigo(codigo)

            result.fold(
                onSuccess = { produtoEncontrado: Produto? ->
                    if (produtoEncontrado != null) {
                        _uiState.value = DetalhesProdutoUiState.Success(produtoEncontrado)
                    } else {
                        _uiState.value = DetalhesProdutoUiState.Error("Produto não encontrado.")
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