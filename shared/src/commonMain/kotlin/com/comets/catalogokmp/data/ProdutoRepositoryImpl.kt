package com.comets.catalogokmp.data

import com.comets.catalogokmp.data.model.Produto
import com.comets.catalogokmp.util.printLogD
import com.comets.catalogokmp.util.printLogE
import com.comets.catalogokmp.util.readProductsJson
import kotlinx.serialization.json.Json

private const val TAG = "ProdutoRepositoryImpl"

class ProdutoRepositoryImpl : ProdutoDataSource {

    private var cachedProdutos: List<Produto>? = null
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    override suspend fun getProdutos(): Result<List<Produto>> {
        cachedProdutos?.let {
            printLogD(TAG, "Retornando ${it.size} produtos do cache.")
            return Result.success(it)
        }

        printLogD(TAG, "Cache vazio. Tentando carregar produtos do JSON.")
        return try {
            val jsonString = readProductsJson()
            printLogD(TAG, "JSON string lida com sucesso. Tamanho: ${jsonString.length}")
            if (jsonString.isBlank()) {
                printLogE(TAG, "JSON string está vazia ou em branco!")
                return Result.failure(Exception("JSON string lida está vazia."))
            }
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)
            cachedProdutos = produtos
            printLogD(TAG, "Produtos parseados e cacheados com sucesso: ${produtos.size} produtos.")
            Result.success(produtos)
        } catch (e: Exception) {
            printLogE(TAG, "Erro ao ler/parsear products.json via KMP resources", e)
            Result.failure(e)
        }
    }

    override suspend fun getProdutoPorCodigo(codigo: String): Result<Produto?> {
        return getProdutos().map { produtos ->
            produtos.find { it.codigo == codigo }
        }
    }
}