package com.comets.catalogokmp.data

import com.comets.catalogokmp.data.model.Produto
import com.comets.catalogokmp.util.readProductsJson
import kotlinx.serialization.json.Json

class ProdutoRepositoryImpl : ProdutoDataSource {

    private var cachedProdutos: List<Produto>? = null
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    override fun getProdutos(): Result<List<Produto>> {
        cachedProdutos?.let {
            return Result.success(it)
        }

        return try {
            val jsonString = readProductsJson()
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)
            cachedProdutos = produtos
            Result.success(produtos)
        } catch (e: Exception) {
            // Captura exceções de IO e de serialização
            Result.failure(e)
        }
    }

    override fun getProdutoPorCodigo(codigo: String): Result<Produto?> {
        return getProdutos().map { produtos ->
            produtos.find { it.codigo == codigo }
        }
    }
}