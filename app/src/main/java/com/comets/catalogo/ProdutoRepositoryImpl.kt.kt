package com.comets.catalogo

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import java.io.IOException

class ProdutoRepositoryImpl : ProdutoDataSource {

    private var cachedProdutos: List<Produto>? = null
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    override fun getProdutos(context: Context): Result<List<Produto>> {
        cachedProdutos?.let {
            return Result.success(it)
        }

        return try {
            val inputStream = context.assets.open("products.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)
            cachedProdutos = produtos
            Log.d("ProdutoRepositoryImpl", "Produtos carregados e cacheados com sucesso.")
            Result.success(produtos)
        } catch (e: IOException) {
            Log.e("ProdutoRepositoryImpl", "Erro ao ler products.json", e)
            Result.failure(e)
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("ProdutoRepositoryImpl", "Erro ao parsear JSON do products.json", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("ProdutoRepositoryImpl", "Erro inesperado ao carregar produtos", e)
            Result.failure(e)
        }
    }

    override fun getProdutoPorCodigo(context: Context, codigo: String): Result<Produto?> {
        return getProdutos(context).map { produtos ->
            produtos.find { it.codigo == codigo }
        }
    }
}