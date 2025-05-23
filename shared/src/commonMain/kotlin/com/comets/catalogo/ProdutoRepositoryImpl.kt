package com.comets.catalogo

import com.comets.catalogo.asset.AssetReadException // Importa a exceção comum
import com.comets.catalogo.asset.AssetReader // Importa a classe expect
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
            // Log (usando uma futura abstração KMP de log, ou tratar no ViewModel)
            Result.failure(e)
        } catch (e: AssetReadException) { // Captura nossa exceção comum
            // Log
            Result.failure(e)
        } catch (e: Exception) { // Captura outras exceções inesperadas
            // Log
            Result.failure(AssetReadException("Erro inesperado ao carregar produtos: ${e.message}", e))
        }
    }

    override fun getProdutoPorCodigo(codigo: String): Result<Produto?> {
        return getProdutos().map { produtos ->
            produtos.find { it.codigo == codigo }
        }
    }
}