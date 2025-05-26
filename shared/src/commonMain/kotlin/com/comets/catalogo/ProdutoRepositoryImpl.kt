package com.comets.catalogo

import com.comets.catalogo.platform.AssetReader
import com.comets.catalogo.platform.AssetReadException
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

class ProdutoRepositoryImpl(private val assetReader: AssetReader) : ProdutoDataSource {

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
            val jsonString = assetReader.lerArquivoTexto("products.json")
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)
            cachedProdutos = produtos
            Result.success(produtos)
        } catch (e: SerializationException) {
            Result.failure(e)
        } catch (e: AssetReadException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(AssetReadException("Erro inesperado ao carregar produtos: ${e.message}", e))
        }
    }

    override fun getProdutoPorCodigo(codigo: String): Result<Produto?> {
        return getProdutos().map { produtos ->
            produtos.find { it.codigo == codigo }
        }
    }
}