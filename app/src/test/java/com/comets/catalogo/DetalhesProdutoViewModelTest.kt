package com.comets.catalogo

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetalhesProdutoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK(relaxed = true)
    private lateinit var mockApplication: Application

    @MockK
    private lateinit var mockSavedStateHandle: SavedStateHandle

    @MockK
    private lateinit var mockProdutoDataSource: ProdutoDataSource

    private lateinit var viewModel: DetalhesProdutoViewModel

    private val sampleProduto = Produto(
        "P001", "Produto Detalhe Teste", "Descrição Detalhe",
        "TipoDetalhe", "LenteDetalhe", "HasteDetalhe", "RoscaDetalhe",
        "imgDetalhe.png", "Detalhes Completos do Produto", "ApelidoDetalhe"
    )
    private val sampleErrorCode = "CODIGO_ERRO"
    private val sampleNonExistentCode = "NAO_EXISTE"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)

        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0

        // Mock das chamadas a getString que o ViewModel faz
        every { mockApplication.getString(R.string.erro_codigo_produto_nao_fornecido) } returns "Código do produto não fornecido."
        every { mockApplication.getString(R.string.produto_nao_encontrado) } returns "Produto não encontrado."
        every { mockApplication.getString(R.string.erro_carregar_detalhes_produto) } returns "Falha ao carregar detalhes do produto."
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    // Helper para criar o ViewModel com mocks específicos para SavedStateHandle
    private fun initializeViewModel(codigoNoHandle: String?) {
        every { mockSavedStateHandle.get<String>("codigo") } returns codigoNoHandle
        viewModel = DetalhesProdutoViewModel(mockApplication, mockSavedStateHandle, mockProdutoDataSource)
    }

    @Test
    fun `init com codigo nulo deve emitir estado de Erro`() = runTest(testDispatcher) {
        initializeViewModel(null) // Código é nulo

        viewModel.uiState.test {
            val  emittedState = awaitItem()
            assertTrue(emittedState is DetalhesProdutoUiState.Error)
            assertEquals("Código do produto não fornecido.", (emittedState as DetalhesProdutoUiState.Error).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `init com codigo valido deve carregar produto com sucesso`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutoPorCodigo(any(), eq(sampleProduto.codigo)) } returns Result.success(sampleProduto)

        initializeViewModel(sampleProduto.codigo)

        viewModel.uiState.test {
            assertEquals("Estado inicial deve ser Loading", DetalhesProdutoUiState.Loading, awaitItem())

            testDispatcher.scheduler.advanceUntilIdle() // Executa loadProduto

            val successState = awaitItem()
            assertTrue("Estado deve ser Success", successState is DetalhesProdutoUiState.Success)
            assertEquals(sampleProduto, (successState as DetalhesProdutoUiState.Success).produto)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `init com codigo valido mas produto nao encontrado deve emitir Erro`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutoPorCodigo(any(), eq(sampleNonExistentCode)) } returns Result.success(null)

        initializeViewModel(sampleNonExistentCode)

        viewModel.uiState.test {
            assertEquals(DetalhesProdutoUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is DetalhesProdutoUiState.Error)
            assertEquals("Produto não encontrado.", (errorState as DetalhesProdutoUiState.Error).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `init com falha ao carregar produto deve emitir Erro`() = runTest(testDispatcher) {
        val exception = IOException("Falha na rede")
        every { mockProdutoDataSource.getProdutoPorCodigo(any(), eq(sampleErrorCode)) } returns Result.failure(exception)

        initializeViewModel(sampleErrorCode)

        viewModel.uiState.test {
            assertEquals(DetalhesProdutoUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is DetalhesProdutoUiState.Error)
            assertEquals("Falha ao carregar detalhes do produto.", (errorState as DetalhesProdutoUiState.Error).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `setIsProcessingPopBack atualiza o estado corretamente`() {
        // O init vai rodar, precisamos de um mock para ele não falhar
        every { mockSavedStateHandle.get<String>("codigo") } returns null // Evita chamada ao repositório
        initializeViewModel(null) // Código nulo para não chamar loadProduto com o repositório

        assertFalse(viewModel.isProcessingPopBack.value)
        viewModel.setIsProcessingPopBack(true)
        assertTrue(viewModel.isProcessingPopBack.value)
        viewModel.setIsProcessingPopBack(false)
        assertFalse(viewModel.isProcessingPopBack.value)
    }
}