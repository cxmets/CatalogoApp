package com.comets.catalogo

import android.app.Application
import android.util.Log
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
class ProdutoListaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK(relaxed = true)
    private lateinit var mockApplication: Application

    @MockK
    private lateinit var mockProdutoDataSource: ProdutoDataSource

    private lateinit var viewModel: ProdutoListaViewModel

    private val sampleProdutos = listOf(
        Produto("P001", "Produto Ágil", "Descrição A", "Tipo1", "LenteX", "HasteA", "RoscaM", "img1.png", "Detalhes A", "Agil"),
        Produto("P002", "Produto Bravo", "Descrição B", "Tipo2", "LenteY", "HasteB", "RoscaN", "img2.png", "Detalhes B", "Bravo Apelido"),
        Produto("P003", "Produto Charlie (Ágil)", "Descrição C", "Tipo1", "LenteX", "HasteC", "RoscaM", "img3.png", "Detalhes C", "Charlie Agil")
    )

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
        every { mockApplication.applicationContext } returns mockApplication
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun initializeViewModel() {
        viewModel = ProdutoListaViewModel(mockApplication, mockProdutoDataSource)
    }

    @Test
    fun `init deve carregar produtos com sucesso e atualizar isLoading`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(sampleProdutos)
        initializeViewModel()
        viewModel.isLoading.test {
            assertTrue(awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertFalse(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        assertEquals(sampleProdutos, viewModel.rawProdutosForDropdowns.value)
        assertNull(viewModel.loadError.value)
    }

    @Test
    fun `init deve tratar falha ao carregar produtos e atualizar isLoading`() = runTest(testDispatcher) {
        val ioException = IOException("Erro de teste")
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.failure(ioException)
        initializeViewModel()
        viewModel.isLoading.test {
            assertTrue(awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertFalse(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        assertTrue(viewModel.rawProdutosForDropdowns.value.isEmpty())
        assertNotNull(viewModel.loadError.value)
        assertEquals("Falha ao carregar catálogo de produtos.", viewModel.loadError.value)
    }

    @Test
    fun `filteredProdutos deve filtrar por searchText corretamente`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(sampleProdutos)
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.filteredProdutos.test {
            // SharingStarted.Lazily inicia com o valor inicial, então a primeira emissão é emptyList
            // A segunda emissão é após o combine ser acionado pelo _rawProdutos e _isLoading
            assertEquals("Valor inicial do stateIn", 0, awaitItem().size)
            assertEquals("Estado após loadProdutos (sem filtros)", 3, awaitItem().size)

            viewModel.updateSearchFilter("Ágil")
            testDispatcher.scheduler.advanceUntilIdle()
            val filteredListA = awaitItem()
            assertEquals("Deve encontrar 2 produtos com 'ágil'", 2, filteredListA.size)
            assertTrue(filteredListA.any { it.codigo == "P001" })
            assertTrue(filteredListA.any { it.codigo == "P003" })

            viewModel.updateSearchFilter("Bravo Apelido")
            testDispatcher.scheduler.advanceUntilIdle()
            val filteredListB = awaitItem()
            assertEquals("Deve encontrar 1 produto com 'Bravo Apelido'", 1, filteredListB.size) // Esta estava falhando
            assertEquals("P002", filteredListB[0].codigo)

            viewModel.updateSearchFilter("NaoExiste")
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue("Não deve encontrar produtos com 'NaoExiste'", awaitItem().isEmpty())

            viewModel.updateSearchFilter("")
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals("Deve retornar todos os produtos ao limpar a busca", sampleProdutos.size, awaitItem().size)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `filteredProdutos deve filtrar por selectedTipo corretamente`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(sampleProdutos)
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.filteredProdutos.test {
            assertEquals(0, awaitItem().size) // initialValue
            assertEquals("Estado inicial deve ter todos os produtos", sampleProdutos.size, awaitItem().size)

            viewModel.updateTipoFilter("Tipo2")
            testDispatcher.scheduler.advanceUntilIdle()
            val filteredList = awaitItem()
            assertEquals("Deve encontrar 1 produto do Tipo2", 1, filteredList.size)
            assertEquals("P002", filteredList[0].codigo)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `filteredProdutos deve combinar searchText e selectedTipo`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(sampleProdutos)
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.filteredProdutos.test {
            assertEquals(0, awaitItem().size) // initialValue
            assertEquals("Inicialmente deve mostrar todos os produtos", 3, awaitItem().size)

            viewModel.updateSearchFilter("Ágil")
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals("Deve encontrar 2 produtos com 'ágil'", 2, awaitItem().size)

            viewModel.updateTipoFilter("Tipo1")
            testDispatcher.scheduler.advanceUntilIdle()
            // Agora, o resultado da lista é o mesmo que antes, então não haverá nova emissão.
            // Verificamos o valor atual e esperamos que não haja mais eventos.
            val filtradosCombinados = viewModel.filteredProdutos.value
            assertEquals("Deve encontrar 2 produtos 'Ágil' do 'Tipo1'", 2, filtradosCombinados.size)
            assertTrue(filtradosCombinados.all { it.nome.contains("Ágil", ignoreCase = true) && it.tipo == "Tipo1" })

            expectNoEvents() // Afirma que não há mais emissões

            cancelAndConsumeRemainingEvents() // Ainda é bom para limpar o coletor
        }
    }

    // Testes para requestScrollToTop, toggleFiltrosVisiveis, etc., permanecem os mesmos
    @Test
    fun `requestScrollToTop envia evento ScrollToTop`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(emptyList())
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiEvents.test {
            viewModel.requestScrollToTop()
            assertEquals(ProdutoListaViewModel.UiEvent.ScrollToTop, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `toggleFiltrosVisiveis alterna o valor de filtrosVisiveis`() = runTest(testDispatcher){
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(emptyList())
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.filtrosVisiveis.value)
        viewModel.toggleFiltrosVisiveis()
        assertTrue(viewModel.filtrosVisiveis.value)
        viewModel.toggleFiltrosVisiveis()
        assertFalse(viewModel.filtrosVisiveis.value)
    }

    @Test
    fun `setExpandedTipo alterna e fecha outros dropdowns`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(emptyList())
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.expandedTipo.value)
        assertFalse(viewModel.expandedLente.value)
        viewModel.setExpandedLente(true)
        assertTrue(viewModel.expandedLente.value)
        viewModel.setExpandedTipo(true)
        assertTrue(viewModel.expandedTipo.value)
        assertFalse(viewModel.expandedLente.value)
        viewModel.setExpandedTipo(false)
        assertFalse(viewModel.expandedTipo.value)
    }

    @Test
    fun `setIsProcessingPopBack atualiza o estado corretamente`() = runTest(testDispatcher) {
        every { mockProdutoDataSource.getProdutos(any()) } returns Result.success(emptyList())
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isProcessingPopBack.value)
        viewModel.setIsProcessingPopBack(true)
        assertTrue(viewModel.isProcessingPopBack.value)
        viewModel.setIsProcessingPopBack(false)
        assertFalse(viewModel.isProcessingPopBack.value)
    }
}