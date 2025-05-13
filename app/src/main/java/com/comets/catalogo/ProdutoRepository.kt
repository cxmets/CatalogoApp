package com.comets.catalogo

import android.content.Context
import android.util.Log // Para logs de erro
import kotlinx.serialization.json.Json
import java.io.IOException

object ProdutoRepository {

    // Cache simples para evitar ler o arquivo repetidamente
    private var cachedProdutos: List<Produto>? = null

    // Configuração do parser JSON (ignora chaves desconhecidas se houver no JSON)
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true // <<< LINHA ADICIONADA/MODIFICADA
    }

    // Agora recebe Context
    fun getProdutos(context: Context): List<Produto> {
        // Retorna cache se já carregado
        cachedProdutos?.let { return it }

        return try {
            // Acessa o AssetManager
            val inputStream = context.assets.open("products.json")
            // Lê o conteúdo do arquivo
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            // Fecha o stream
            inputStream.close()
            // Parseia a string JSON para uma lista de Produtos
            val produtos = jsonParser.decodeFromString<List<Produto>>(jsonString)
            // Armazena em cache e retorna
            cachedProdutos = produtos
            produtos
        } catch (e: IOException) {
            // Erro ao ler o arquivo
            Log.e("ProdutoRepository", "Erro ao ler products.json", e)
            emptyList() // Retorna lista vazia em caso de erro
        } catch (e: kotlinx.serialization.SerializationException) {
            // Erro ao parsear o JSON
            Log.e("ProdutoRepository", "Erro ao parsear JSON do products.json", e)
            emptyList() // Retorna lista vazia em caso de erro
        } catch (e: Exception) {
            // Outros erros inesperados
            Log.e("ProdutoRepository", "Erro inesperado ao carregar produtos", e)
            emptyList()
        }
    }

    fun getProdutoPorCodigo(context: Context, codigo: String): Produto? {
        return getProdutos(context).find { it.codigo == codigo }
    }
}