package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.model.Produto

sealed interface DetalhesProdutoUiState {
    object Loading : DetalhesProdutoUiState
    data class Success(val produto: Produto) : DetalhesProdutoUiState
    data class Error(val message: String) : DetalhesProdutoUiState
}