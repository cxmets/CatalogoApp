package com.comets.catalogo

import app.cash.turbine.test // Para Turbine: awaitItem, cancelAndConsumeRemainingEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher // Para TestDispatcher
import kotlinx.coroutines.test.resetMain // Para resetMain
import kotlinx.coroutines.test.runTest // Para o builder runTest
import kotlinx.coroutines.test.setMain // Para setMain
import org.junit.After // Para a anotação @After
import org.junit.Assert.* // Para assertEquals, assertTrue, etc.
import org.junit.Before // Para a anotação @Before
import org.junit.Test // Para a anotação @Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchTextChanged atualiza searchText StateFlow corretamente`() = runTest {
        val viewModel = AppViewModel()
        val novoTextoBusca = "Retrô"

        viewModel.searchText.test {
            assertEquals("Valor inicial do searchText deve ser vazio", "", awaitItem())

            viewModel.onSearchTextChanged(novoTextoBusca)
            assertEquals("searchText deve ser atualizado para o novo texto", novoTextoBusca, awaitItem())

            viewModel.onSearchTextChanged("")
            assertEquals("searchText deve ser atualizado para vazio", "", awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onTipoSelected atualiza selectedTipo StateFlow corretamente`() = runTest {
        val viewModel = AppViewModel()
        val novoTipo = "Esportivo"

        viewModel.selectedTipo.test {
            assertEquals("Valor inicial do selectedTipo deve ser vazio", "", awaitItem())
            viewModel.onTipoSelected(novoTipo)
            assertEquals("selectedTipo deve ser atualizado", novoTipo, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearAllFilters zera todos os filtros corretamente`() = runTest {
        val viewModel = AppViewModel()

        viewModel.onSearchTextChanged("teste")
        viewModel.onTipoSelected("TIPO A")
        viewModel.onLenteSelected("LENTE X")
        viewModel.onHasteSelected("HASTE Y")
        viewModel.onRoscaSelected("ROSCA Z")

        viewModel.clearAllFilters()

        assertEquals("", viewModel.searchText.value)
        assertEquals("", viewModel.selectedTipo.value)
        assertEquals("", viewModel.selectedLente.value)
        assertEquals("", viewModel.selectedHaste.value)
        assertEquals("", viewModel.selectedRosca.value)
    }
}