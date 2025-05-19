package com.comets.catalogo

import android.content.Context

interface ProdutoDataSource {
    fun getProdutos(context: Context): Result<List<Produto>>
    fun getProdutoPorCodigo(context: Context, codigo: String): Result<Produto?>
}