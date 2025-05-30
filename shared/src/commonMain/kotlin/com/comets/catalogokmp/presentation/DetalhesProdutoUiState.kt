package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.model.Produto
import org.jetbrains.compose.resources.StringResource

sealed interface DetalhesProdutoUiState {
    data object Loading : DetalhesProdutoUiState
    data class Success(val produto: Produto) : DetalhesProdutoUiState
    data class Error(val messageResource: StringResource) : DetalhesProdutoUiState
}