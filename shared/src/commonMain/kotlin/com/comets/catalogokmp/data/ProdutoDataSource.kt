package com.comets.catalogokmp.data

import com.comets.catalogokmp.data.model.Produto

interface ProdutoDataSource {
    fun getProdutos(): Result<List<Produto>>
    fun getProdutoPorCodigo(codigo: String): Result<Produto?>
}