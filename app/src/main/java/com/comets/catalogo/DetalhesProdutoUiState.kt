package com.comets.catalogo

sealed interface DetalhesProdutoUiState {
    object Loading : DetalhesProdutoUiState
    data class Success(val produto: Produto) : DetalhesProdutoUiState
    data class Error(val message: String) : DetalhesProdutoUiState
}