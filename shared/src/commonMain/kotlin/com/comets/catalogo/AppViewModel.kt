package com.comets.catalogo

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _searchText = MutableStateFlow(viewModelScope, "")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedTipo = MutableStateFlow(viewModelScope, "")
    val selectedTipo: StateFlow<String> = _selectedTipo.asStateFlow()

    private val _selectedLente = MutableStateFlow(viewModelScope, "")
    val selectedLente: StateFlow<String> = _selectedLente.asStateFlow()

    private val _selectedHaste = MutableStateFlow(viewModelScope, "")
    val selectedHaste: StateFlow<String> = _selectedHaste.asStateFlow()

    private val _selectedRosca = MutableStateFlow(viewModelScope, "")
    val selectedRosca: StateFlow<String> = _selectedRosca.asStateFlow()

    fun onSearchTextChanged(newSearchText: String) {
        _searchText.value = newSearchText
    }

    fun onTipoSelected(newTipo: String) {
        _selectedTipo.value = newTipo
    }

    fun onLenteSelected(newLente: String) {
        _selectedLente.value = newLente
    }

    fun onHasteSelected(newHaste: String) {
        _selectedHaste.value = newHaste
    }

    fun onRoscaSelected(newRosca: String) {
        _selectedRosca.value = newRosca
    }

    fun clearAllFilters() {
        _searchText.value = ""
        _selectedTipo.value = ""
        _selectedLente.value = ""
        _selectedHaste.value = ""
        _selectedRosca.value = ""
    }
}