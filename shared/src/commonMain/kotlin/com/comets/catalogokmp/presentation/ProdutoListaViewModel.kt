package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.model.Produto
import com.comets.catalogokmp.model.SortCriteria
import com.comets.catalogokmp.model.SortDirection
import com.comets.catalogokmp.model.SortOption
import com.comets.catalogokmp.util.normalizeForSearch
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.falha_carregar_catalogo_produtos

private fun getCodeComparator(): Comparator<String> {
    return Comparator { s1, s2 ->
        val n1 = s1.toLongOrNull()
        val n2 = s2.toLongOrNull()

        if (n1 != null && n2 != null) {
            n1.compareTo(n2)
        } else if (n1 != null) {
            -1
        } else if (n2 != null) {
            1
        } else {
            s1.compareTo(s2, ignoreCase = true)
        }
    }
}

class ProdutoListaViewModel(
    private val produtoDataSource: ProdutoDataSource,
    appViewModel: AppViewModel
) : ViewModel() {

    private val _rawProdutos = MutableStateFlow<List<Produto>>(emptyList())
    val rawProdutosForDropdowns: StateFlow<List<Produto>> = _rawProdutos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow<StringResource?>(null)
    val loadError: StateFlow<StringResource?> = _loadError.asStateFlow()

    private val _currentSearchText = MutableStateFlow(appViewModel.searchText.value)
    private val _currentSelectedTipo = MutableStateFlow(appViewModel.selectedTipo.value)
    private val _currentSelectedLente = MutableStateFlow(appViewModel.selectedLente.value)
    private val _currentSelectedHaste = MutableStateFlow(appViewModel.selectedHaste.value)
    private val _currentSelectedRosca = MutableStateFlow(appViewModel.selectedRosca.value)
    private val _currentSortOption = MutableStateFlow(appViewModel.selectedSortOption.value)

    val filteredProdutos: StateFlow<List<Produto>> = combine(
        _rawProdutos,
        _currentSearchText,
        _currentSelectedTipo,
        _currentSelectedLente,
        _currentSelectedHaste,
        _currentSelectedRosca,
        _currentSortOption
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val produtos = values[0] as? List<Produto> ?: emptyList()
        val searchText = values[1] as? String ?: ""
        val tipo = values[2] as? String ?: ""
        val lente = values[3] as? String ?: ""
        val haste = values[4] as? String ?: ""
        val rosca = values[5] as? String ?: ""
        val sortOption = values[6] as? SortOption ?: SortOption.DEFAULT

        if (_isLoading.value || _loadError.value != null) {
            emptyList<Produto>()
        } else {
            val filteredList = produtos.filter { produto ->
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

            val sortedList = when (sortOption.criteria) {
                SortCriteria.NAME -> {
                    if (sortOption.direction == SortDirection.ASC) {
                        filteredList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.nome })
                    } else {
                        filteredList.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.nome })
                    }
                }
                SortCriteria.CODE -> {
                    val codeComparator = getCodeComparator()
                    if (sortOption.direction == SortDirection.ASC) {
                        filteredList.sortedWith(compareBy(codeComparator) { it.codigo })
                    } else {
                        filteredList.sortedWith(compareByDescending(codeComparator) { it.codigo })
                    }
                }
                SortCriteria.TYPE -> {
                    if (sortOption.direction == SortDirection.ASC) {
                        filteredList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.tipo })
                    } else {
                        filteredList.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.tipo })
                    }
                }
            }
            sortedList
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList<Produto>())

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

    private val _isTransitioningOut = MutableStateFlow(false)
    val isTransitioningOut: StateFlow<Boolean> = _isTransitioningOut.asStateFlow()

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents: Flow<UiEvent> = _uiEvents.receiveAsFlow()

    sealed class UiEvent {
        data object ScrollToTop : UiEvent()
    }

    init {
        loadProdutos()
    }

    private fun loadProdutos() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadError.value = null
            val result = produtoDataSource.getProdutos()
            result.fold(
                onSuccess = { produtos ->
                    _rawProdutos.value = produtos
                },
                onFailure = {
                    _rawProdutos.value = emptyList()
                    _loadError.value = Res.string.falha_carregar_catalogo_produtos
                }
            )
            _isLoading.value = false
        }
    }

    fun updateSearchFilter(text: String) {
        if (_currentSearchText.value != text) {
            _currentSearchText.value = text
            requestScrollToTop()
        }
    }
    fun updateTipoFilter(tipo: String) {
        if (_currentSelectedTipo.value != tipo) {
            _currentSelectedTipo.value = tipo
            requestScrollToTop()
        }
    }
    fun updateLenteFilter(lente: String) {
        if (_currentSelectedLente.value != lente) {
            _currentSelectedLente.value = lente
            requestScrollToTop()
        }
    }
    fun updateHasteFilter(haste: String) {
        if (_currentSelectedHaste.value != haste) {
            _currentSelectedHaste.value = haste
            requestScrollToTop()
        }
    }
    fun updateRoscaFilter(rosca: String) {
        if (_currentSelectedRosca.value != rosca) {
            _currentSelectedRosca.value = rosca
            requestScrollToTop()
        }
    }
    fun updateSortOption(sortOption: SortOption) {
        if (_currentSortOption.value != sortOption) {
            _currentSortOption.value = sortOption
            requestScrollToTop()
        }
    }

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

    fun setIsProcessingPopBack(isProcessing: Boolean) {
        _isProcessingPopBack.value = isProcessing
    }

    fun setScreenTransitioningOut(isTransitioning: Boolean) {
        _isTransitioningOut.value = isTransitioning
    }

    fun requestScrollToTop() {
        _uiEvents.trySend(UiEvent.ScrollToTop)
    }
}