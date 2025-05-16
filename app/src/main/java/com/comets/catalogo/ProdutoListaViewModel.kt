package com.comets.catalogo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.Normalizer

// Função de extensão para normalizar strings para busca
private fun String.normalizeForSearch(): String {
    val normalizedText = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalizedText, "").lowercase().trim()
}

class ProdutoListaViewModel(application: Application) : AndroidViewModel(application) {

    private val _rawProdutos = MutableStateFlow<List<Produto>>(emptyList())
    val rawProdutosForDropdowns: StateFlow<List<Produto>> = _rawProdutos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentSearchText = MutableStateFlow("")
    private val _currentSelectedTipo = MutableStateFlow("")
    private val _currentSelectedLente = MutableStateFlow("")
    private val _currentSelectedHaste = MutableStateFlow("")
    private val _currentSelectedRosca = MutableStateFlow("")

    val filteredProdutos: StateFlow<List<Produto>> = combine(
        // Lista de fluxos a serem combinados
        listOf(
            _rawProdutos,
            _currentSearchText,
            _currentSelectedTipo,
            _currentSelectedLente,
            _currentSelectedHaste,
            _currentSelectedRosca
        )
    ) { values -> // 'values' é um Array<Any?> com os últimos valores de cada fluxo
        @Suppress("UNCHECKED_CAST")
        val produtos = values[0] as? List<Produto> ?: emptyList()
        val searchText = values[1] as? String ?: ""
        val tipo = values[2] as? String ?: ""
        val lente = values[3] as? String ?: ""
        val haste = values[4] as? String ?: ""
        val rosca = values[5] as? String ?: ""

        if (_isLoading.value) {
            emptyList<Produto>() // Retorna List<Produto> vazia se estiver carregando
        } else {
            produtos.filter { produto -> // 'produto' aqui é do tipo Produto
                val searchMatch = if (searchText.isBlank()) {
                    true
                } else {
                    val searchTerms = searchText.normalizeForSearch().split(Regex("\\s+")).filter { it.isNotEmpty() }
                    if (searchTerms.isEmpty()) true else {
                        val productText = (produto.nome.normalizeForSearch() + " " +
                                produto.codigo.normalizeForSearch() + " " +
                                produto.apelido.normalizeForSearch())
                        searchTerms.all { term -> productText.contains(term) }
                    }
                }
                // Acesso aos campos de 'produto' (ex: produto.tipo) deve funcionar agora
                val tipoMatch = tipo.isEmpty() || produto.tipo.equals(tipo, ignoreCase = true)
                val lenteMatch = lente.isEmpty() || produto.lente.equals(lente, ignoreCase = true)
                val hasteMatch = haste.isEmpty() || produto.haste.equals(haste, ignoreCase = true)
                val roscaMatch = rosca.isEmpty() || produto.rosca.equals(rosca, ignoreCase = true)

                searchMatch && tipoMatch && lenteMatch && hasteMatch && roscaMatch
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList<Produto>()) // Valor inicial tipado

    private val _filtrosVisiveis = MutableStateFlow(false)
    val filtrosVisiveis: StateFlow<Boolean> = _filtrosVisiveis.asStateFlow()

    private val _expandedTipo = MutableStateFlow(false)
    val expandedTipo: StateFlow<Boolean> = _expandedTipo.asStateFlow()

    private val _expandedLente = MutableStateFlow(false)
    val expandedLente: StateFlow<Boolean> = _expandedLente.asStateFlow()

    private val _expandedHaste = MutableStateFlow(false)
    val expandedHaste: StateFlow<Boolean> = _expandedHaste.asStateFlow()

    private val _expandedRosca = MutableStateFlow(false)
    val expandedRosca: StateFlow<Boolean> = _expandedRosca.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents: Flow<UiEvent> = _uiEvents.receiveAsFlow()

    sealed class UiEvent {
        object ScrollToTop : UiEvent()
    }

    init {
        loadProdutos()
    }

    private fun loadProdutos() {
        viewModelScope.launch {
            _isLoading.value = true
            _rawProdutos.value = ProdutoRepository.getProdutos(getApplication<Application>().applicationContext)
            _isLoading.value = false
        }
    }

    fun updateSearchFilter(text: String) { _currentSearchText.value = text }
    fun updateTipoFilter(tipo: String) { _currentSelectedTipo.value = tipo }
    fun updateLenteFilter(lente: String) { _currentSelectedLente.value = lente }
    fun updateHasteFilter(haste: String) { _currentSelectedHaste.value = haste }
    fun updateRoscaFilter(rosca: String) { _currentSelectedRosca.value = rosca }

    fun toggleFiltrosVisiveis() { _filtrosVisiveis.value = !_filtrosVisiveis.value }

    private fun closeAllDropdownsInternal() {
        _expandedTipo.value = false
        _expandedLente.value = false
        _expandedHaste.value = false
        _expandedRosca.value = false
    }

    fun setExpandedTipo(isExpanded: Boolean) { if(isExpanded) closeAllDropdownsInternal(); _expandedTipo.value = isExpanded }
    fun setExpandedLente(isExpanded: Boolean) { if(isExpanded) closeAllDropdownsInternal(); _expandedLente.value = isExpanded }
    fun setExpandedHaste(isExpanded: Boolean) { if(isExpanded) closeAllDropdownsInternal(); _expandedHaste.value = isExpanded }
    fun setExpandedRosca(isExpanded: Boolean) { if(isExpanded) closeAllDropdownsInternal(); _expandedRosca.value = isExpanded }

    fun closeAllDropdownsUiAction() {
        closeAllDropdownsInternal()
    }

    fun setIsProcessingPopBack(isProcessing: Boolean) { _isProcessingPopBack.value = isProcessing }

    fun requestScrollToTop() {
        _uiEvents.trySend(UiEvent.ScrollToTop)
    }
}