package com.comets.catalogo

import android.content.Context
import android.content.res.AssetManager
import android.util.Log // Import para android.util.Log
import io.mockk.* // Importa tudo do MockK
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import kotlinx.serialization.SerializationException

class ProdutoRepositoryImplTest {

    @MockK
    private lateinit var mockContext: Context

    @MockK(relaxed = true)
    private lateinit var mockAssetManager: AssetManager

    private lateinit var repository: ProdutoRepositoryImpl

    private val sampleJsonSuccess = """
        [
            {
                "codigo": "P001", "nome": "Produto Teste 1", "descricao": "Desc 1", 
                "tipo": "TipoA", "lente": "LenteX", "haste": "HasteY", "rosca": "RoscaZ", 
                "imagemUrl": "img1.png", "detalhes": "Detalhes 1", "apelido": "PT1"
            },
            {
                "codigo": "P002", "nome": "Produto Teste 2", "descricao": "Desc 2", 
                "tipo": "TipoB", "lente": "LenteW", "haste": "HasteV", "rosca": "RoscaU", 
                "imagemUrl": "img2.png", "detalhes": "Detalhes 2", "apelido": ""
            }
        ]
    """.trimIndent()

    private val malformedJson = """[{"codigo": "P001", "nome": "Produto Teste 1", "tipo":,}]"""

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true) // relaxUnitFun para funções que retornam Unit

        // Mock para android.util.Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0 // Para Log.d(tag, msg)
        every { Log.e(any(), any()) } returns 0 // Para Log.e(tag, msg)
        every { Log.e(any(), any(), any()) } returns 0 // Para Log.e(tag, msg, throwable)
        // Adicione outros níveis de log (v, i, w) se seu repositório os usar.

        every { mockContext.assets } returns mockAssetManager
        repository = ProdutoRepositoryImpl()
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class) // Limpa o mock estático do Log
    }

    private fun mockJsonAsset(jsonString: String): InputStream {
        return ByteArrayInputStream(jsonString.toByteArray(Charsets.UTF_8))
    }

    @Test
    fun `getProdutos com sucesso deve retornar lista de produtos e popular cache`() {
        every { mockAssetManager.open("products.json") } returns mockJsonAsset(sampleJsonSuccess)

        val result1 = repository.getProdutos(mockContext)

        assertTrue("Resultado deve ser sucesso", result1.isSuccess)
        val produtos1 = result1.getOrNull()
        assertNotNull("Lista de produtos não deve ser nula", produtos1)
        assertEquals("Deve haver 2 produtos", 2, produtos1?.size)
        assertEquals("Código do primeiro produto deve ser P001", "P001", produtos1?.get(0)?.codigo)
        assertEquals("Nome do segundo produto deve ser Produto Teste 2", "Produto Teste 2", produtos1?.get(1)?.nome)

        val result2 = repository.getProdutos(mockContext)
        assertTrue("Resultado da segunda chamada deve ser sucesso", result2.isSuccess)
        val produtos2 = result2.getOrNull()
        assertEquals("Lista da segunda chamada deve ser igual à primeira (cache)", produtos1, produtos2)

        verify(exactly = 1) { mockAssetManager.open("products.json") }
    }

    @Test
    fun `getProdutos com IOException ao abrir asset deve retornar falha`() {
        val errorMessage = "Erro simulado ao abrir asset"
        every { mockAssetManager.open("products.json") } throws IOException(errorMessage)

        val result = repository.getProdutos(mockContext)

        assertTrue("Resultado deve ser falha", result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exceção deve ser IOException", exception is IOException)
        assertEquals(errorMessage, exception?.message)
    }

    @Test
    fun `getProdutos com JSON malformado deve retornar falha de SerializationException`() {
        every { mockAssetManager.open("products.json") } returns mockJsonAsset(malformedJson)

        val result = repository.getProdutos(mockContext)

        assertTrue("Resultado deve ser falha devido a JSON malformado", result.isFailure)
        assertTrue("Exceção deve ser SerializationException", result.exceptionOrNull() is SerializationException)
    }

    @Test
    fun `getProdutos com cache preenchido deve retornar cache sem ler arquivo`() {
        every { mockAssetManager.open("products.json") } returns mockJsonAsset(sampleJsonSuccess)
        repository.getProdutos(mockContext)

        val result = repository.getProdutos(mockContext)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)

        verify(exactly = 1) { mockAssetManager.open("products.json") }
    }

    @Test
    fun `getProdutoPorCodigo com codigo existente deve retornar produto`() {
        every { mockAssetManager.open("products.json") } returns mockJsonAsset(sampleJsonSuccess)

        val result = repository.getProdutoPorCodigo(mockContext, "P001")

        assertTrue(result.isSuccess)
        val produto = result.getOrNull()
        assertNotNull(produto)
        assertEquals("P001", produto?.codigo)
        assertEquals("Produto Teste 1", produto?.nome)
    }

    @Test
    fun `getProdutoPorCodigo com codigo inexistente deve retornar null dentro de Result_success`() {
        every { mockAssetManager.open("products.json") } returns mockJsonAsset(sampleJsonSuccess)

        val result = repository.getProdutoPorCodigo(mockContext, "P999_NAO_EXISTE")

        assertTrue("Busca por código inexistente deve ser uma operação bem-sucedida", result.isSuccess)
        assertNull("Produto retornado deve ser null para código inexistente", result.getOrNull())
    }

    @Test
    fun `getProdutoPorCodigo quando getProdutos falha deve retornar falha`() {
        every { mockAssetManager.open("products.json") } throws IOException("Erro ao carregar lista base")

        val result = repository.getProdutoPorCodigo(mockContext, "P001")

        assertTrue("getProdutoPorCodigo deve falhar se getProdutos falhar", result.isFailure)
        assertTrue("Exceção deve ser propagada", result.exceptionOrNull() is IOException)
    }
}