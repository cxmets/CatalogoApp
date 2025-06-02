package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.model.SortOption
import com.comets.catalogokmp.model.UserThemePreference
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val THEME_PREFERENCE_KEY = "user_theme_preference_v1"
private const val SORT_OPTION_PREFERENCE_KEY = "user_sort_option_preference_v1"

class AppViewModel(
    private val produtoDataSource: ProdutoDataSource,
    private val settings: Settings
) : ViewModel() {

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

    private val _selectedSortOption = MutableStateFlow(SortOption.DEFAULT)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    private val _userThemePreference = MutableStateFlow(UserThemePreference.SYSTEM)
    val userThemePreference: StateFlow<UserThemePreference> = _userThemePreference.asStateFlow()

    init {
        viewModelScope.launch {
            produtoDataSource.getProdutos()
        }
        loadThemePreference()
        loadSortOptionPreference()
    }

    private fun loadThemePreference() {
        val savedPreferenceName = settings.getStringOrNull(THEME_PREFERENCE_KEY)
        if (savedPreferenceName != null) {
            try {
                _userThemePreference.value = UserThemePreference.valueOf(savedPreferenceName)
            } catch (_: IllegalArgumentException) {
                _userThemePreference.value = UserThemePreference.SYSTEM
                settings.remove(THEME_PREFERENCE_KEY)
            }
        } else {
            _userThemePreference.value = UserThemePreference.SYSTEM
        }
    }

    private fun loadSortOptionPreference() {
        val savedSortOptionKey = settings.getStringOrNull(SORT_OPTION_PREFERENCE_KEY)
        if (savedSortOptionKey != null) {
            _selectedSortOption.value = SortOption.allOptions.find {
                it.displayNameResource.key == savedSortOptionKey
            } ?: SortOption.DEFAULT

            if (_selectedSortOption.value == SortOption.DEFAULT && SortOption.DEFAULT.displayNameResource.key != savedSortOptionKey) {
                settings.remove(SORT_OPTION_PREFERENCE_KEY)
            }
        } else {
            _selectedSortOption.value = SortOption.DEFAULT
        }
    }

    fun setUserThemePreference(preference: UserThemePreference) {
        _userThemePreference.value = preference
        settings.putString(THEME_PREFERENCE_KEY, preference.name)
    }

    fun onSortOptionSelected(sortOption: SortOption) {
        _selectedSortOption.value = sortOption
        settings.putString(SORT_OPTION_PREFERENCE_KEY, sortOption.displayNameResource.key)
    }

    fun onSearchTextChanged(newSearchText: String) { _searchText.value = newSearchText }
    fun onTipoSelected(newTipo: String) { _selectedTipo.value = newTipo }
    fun onLenteSelected(newLente: String) { _selectedLente.value = newLente }
    fun onHasteSelected(newHaste: String) { _selectedHaste.value = newHaste }
    fun onRoscaSelected(newRosca: String) { _selectedRosca.value = newRosca }

    fun clearAllFilters() {
        _searchText.value = ""
        _selectedTipo.value = ""
        _selectedLente.value = ""
        _selectedHaste.value = ""
        _selectedRosca.value = ""
    }
}