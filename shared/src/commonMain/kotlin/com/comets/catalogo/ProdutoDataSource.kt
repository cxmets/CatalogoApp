package com.comets.catalogo

interface ProdutoDataSource {
    fun getProdutos(): Result<List<Produto>> // Context removido
    fun getProdutoPorCodigo(codigo: String): Result<Produto?> // Context removido
}