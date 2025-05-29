package com.comets.catalogokmp.data

import com.comets.catalogokmp.data.model.Produto

interface ProdutoDataSource {
    suspend fun getProdutos(): Result<List<Produto>>
    suspend fun getProdutoPorCodigo(codigo: String): Result<Produto?>
}