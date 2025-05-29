package com.comets.catalogokmp.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedTipo = MutableStateFlow("")
    val selectedTipo: StateFlow<String> = _selectedTipo.asStateFlow()

    private val _selectedLente = MutableStateFlow("")
    val selectedLente: StateFlow<String> = _selectedLente.asStateFlow()

    private val _selectedHaste = MutableStateFlow("")
    val selectedHaste: StateFlow<String> = _selectedHaste.asStateFlow()

    private val _selectedRosca = MutableStateFlow("")
    val selectedRosca: StateFlow<String> = _selectedRosca.asStateFlow()

    // Novo SharedFlow para o evento de "voltar de detalhes"
    private val _onNavigateBackFromDetails = MutableSharedFlow<Unit>(replay = 0) // replay=0 para que seja como um evento
    val onNavigateBackFromDetails = _onNavigateBackFromDetails.asSharedFlow()

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

    fun triggerNavigatedBackFromDetails() {
        viewModelScope.launch {
            clearAllFilters() // A lógica de limpar filtros agora está aqui
            _onNavigateBackFromDetails.emit(Unit)
        }
    }
}