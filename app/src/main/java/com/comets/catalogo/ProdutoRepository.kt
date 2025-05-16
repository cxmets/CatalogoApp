package com.comets.catalogo

import android.content.Context
import android.util.Log // Para logs de erro
import kotlinx.serialization.json.Json
import java.io.IOException

object ProdutoRepository {

    private var cachedProdutos: List<Produto>? = null

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun getProdutos(context: Context): List<Produto> {

        cachedProdutos?.let { return it }

        return try {
            val inputStream = context.assets.open("products.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            inputStream.close()
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)

            cachedProdutos = produtos
            produtos
        } catch (e: IOException) {
            Log.e("ProdutoRepository", "Erro ao ler products.json", e)
            emptyList()
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("ProdutoRepository", "Erro ao parsear JSON do products.json", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("ProdutoRepository", "Erro inesperado ao carregar produtos", e)
            emptyList()
        }
    }

    fun getProdutoPorCodigo(context: Context, codigo: String): Produto? {
        return getProdutos(context).find { it.codigo == codigo }
    }
}