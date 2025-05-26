package com.comets.catalogo

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.stateIn
import com.comets.catalogo.platform.normalizeForSearch
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch // Import para kotlinx.coroutines.launch

class ProdutoListaViewModel(
    private val produtoDataSource: ProdutoDataSource
) : ViewModel() {

    private val _rawProdutos = MutableStateFlow(viewModelScope, emptyList<Produto>())
    val rawProdutosForDropdowns: StateFlow<List<Produto>> = _rawProdutos.asStateFlow()

    private val _isLoading = MutableStateFlow(viewModelScope, true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(viewModelScope, null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    private val _currentSearchText = MutableStateFlow(viewModelScope, "")
    private val _currentSelectedTipo = MutableStateFlow(viewModelScope, "")
    private val _currentSelectedLente = MutableStateFlow(viewModelScope, "")
    private val _currentSelectedHaste = MutableStateFlow(viewModelScope, "")
    private val _currentSelectedRosca = MutableStateFlow(viewModelScope, "")

    val filteredProdutos: StateFlow<List<Produto>> = combine(
        _rawProdutos, _currentSearchText, _currentSelectedTipo,
        _currentSelectedLente, _currentSelectedHaste, _currentSelectedRosca
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val produtos = values[0] as? List<Produto> ?: emptyList()
        val searchText = values[1] as? String ?: ""
        val tipo = values[2] as? String ?: ""
        val lente = values[3] as? String ?: ""
        val haste = values[4] as? String ?: ""
        val rosca = values[5] as? String ?: ""

        if (_isLoading.value || _loadError.value != null) {
            emptyList<Produto>()
        } else {
            produtos.filter { produto ->
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
                val tipoMatch = tipo.isEmpty() || produto.tipo.equals(tipo, ignoreCase = true)
                val lenteMatch = lente.isEmpty() || produto.lente.equals(lente, ignoreCase = true)
                val hasteMatch = haste.isEmpty() || produto.haste.equals(haste, ignoreCase = true)
                val roscaMatch = rosca.isEmpty() || produto.rosca.equals(rosca, ignoreCase = true)

                searchMatch && tipoMatch && lenteMatch && hasteMatch && roscaMatch
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList<Produto>())

    private val _filtrosVisiveis = MutableStateFlow(viewModelScope, false)
    val filtrosVisiveis: StateFlow<Boolean> = _filtrosVisiveis.asStateFlow()

    private val _expandedTipo = MutableStateFlow(viewModelScope, false)
    val expandedTipo: StateFlow<Boolean> = _expandedTipo.asStateFlow()

    private val _expandedLente = MutableStateFlow(viewModelScope, false)
    val expandedLente: StateFlow<Boolean> = _expandedLente.asStateFlow()

    private val _expandedHaste = MutableStateFlow(viewModelScope, false)
    val expandedHaste: StateFlow<Boolean> = _expandedHaste.asStateFlow()

    private val _expandedRosca = MutableStateFlow(viewModelScope, false)
    val expandedRosca: StateFlow<Boolean> = _expandedRosca.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(viewModelScope, false)
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
        viewModelScope.coroutineScope.launch { // <<< CORREÇÃO AQUI
            _isLoading.value = true
            _loadError.value = null
            val result = produtoDataSource.getProdutos()
            result.fold(
                onSuccess = { produtos ->
                    _rawProdutos.value = produtos
                },
                onFailure = {
                    _rawProdutos.value = emptyList()
                    _loadError.value = "Falha ao carregar catálogo de produtos."
                }
            )
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