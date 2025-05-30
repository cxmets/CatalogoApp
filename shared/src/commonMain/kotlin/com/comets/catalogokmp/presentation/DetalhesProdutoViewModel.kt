package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.model.Produto
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.erro_carregar_detalhes_produto
import catalogokmp.shared.generated.resources.erro_codigo_produto_nao_fornecido
import catalogokmp.shared.generated.resources.produto_nao_encontrado

class DetalhesProdutoViewModel(
    private val produtoDataSource: ProdutoDataSource,
    codigoProduto: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalhesProdutoUiState>(DetalhesProdutoUiState.Loading)
    val uiState: StateFlow<DetalhesProdutoUiState> = _uiState.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    init {
        if (codigoProduto != null) {
            loadProduto(codigoProduto)
        } else {
            _uiState.value = DetalhesProdutoUiState.Error(Res.string.erro_codigo_produto_nao_fornecido)
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
                        _uiState.value = DetalhesProdutoUiState.Error(Res.string.produto_nao_encontrado)
                    }
                },
                onFailure = {
                    _uiState.value = DetalhesProdutoUiState.Error(Res.string.erro_carregar_detalhes_produto)
                }
            )
        }
    }

    fun setIsProcessingPopBack(isProcessing: Boolean) {
        _isProcessingPopBack.value = isProcessing
    }
}