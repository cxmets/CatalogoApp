package com.comets.catalogo

interface ProdutoDataSource {
    fun getProdutos(): Result<List<Produto>>
    fun getProdutoPorCodigo(codigo: String): Result<Produto?>
}