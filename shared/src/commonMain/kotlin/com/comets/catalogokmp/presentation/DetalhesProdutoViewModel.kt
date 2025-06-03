package com.comets.catalogokmp.presentation

import com.comets.catalogokmp.data.ProdutoDataSource
import com.comets.catalogokmp.data.model.Produto
import com.comets.catalogokmp.model.SelectableSimilarityCriterion
import com.comets.catalogokmp.model.SimilarityCriteriaList
import com.comets.catalogokmp.model.SimilarityCriterionKey
import com.comets.catalogokmp.util.normalizeForSearch
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.erro_carregar_detalhes_produto
import catalogokmp.shared.generated.resources.erro_codigo_produto_nao_fornecido
import catalogokmp.shared.generated.resources.produto_nao_encontrado
import kotlin.math.max
import kotlin.math.min

class DetalhesProdutoViewModel(
    private val produtoDataSource: ProdutoDataSource,
    currentCodigoProduto: String?,
    private val appViewModel: AppViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalhesProdutoUiState>(DetalhesProdutoUiState.Loading)
    val uiState: StateFlow<DetalhesProdutoUiState> = _uiState.asStateFlow()

    private val _isProcessingPopBack = MutableStateFlow(false)
    val isProcessingPopBack: StateFlow<Boolean> = _isProcessingPopBack.asStateFlow()

    private val _similarProdutos = MutableStateFlow<List<Produto>>(emptyList())
    val similarProdutos: StateFlow<List<Produto>> = _similarProdutos.asStateFlow()

    val userSelectedSimilarityCriteriaKeys: StateFlow<Set<SimilarityCriterionKey>> = appViewModel.userSelectedSimilarityCriteriaKeys

    private val _availableCriteriaForSelection = MutableStateFlow<List<SelectableSimilarityCriterion>>(emptyList())
    val availableCriteriaForSelection: StateFlow<List<SelectableSimilarityCriterion>> = _availableCriteriaForSelection.asStateFlow()

    private val _showSimilarityCriteriaSelectionButton = MutableStateFlow(false)
    val showSimilarityCriteriaSelectionButton: StateFlow<Boolean> = _showSimilarityCriteriaSelectionButton.asStateFlow()

    init {
        if (currentCodigoProduto != null) {
            loadProdutoAndSimilar(currentCodigoProduto)
        } else {
            _uiState.value = DetalhesProdutoUiState.Error(Res.string.erro_codigo_produto_nao_fornecido)
        }
    }

    private fun loadProdutoAndSimilar(codigo: String) {
        viewModelScope.launch {
            _uiState.value = DetalhesProdutoUiState.Loading
            _similarProdutos.value = emptyList()

            val result = produtoDataSource.getProdutoPorCodigo(codigo)
            val currentSelectedKeys = appViewModel.userSelectedSimilarityCriteriaKeys.first() // Pega o valor atual

            result.fold(
                onSuccess = { produtoEncontrado ->
                    if (produtoEncontrado != null) {
                        _uiState.value = DetalhesProdutoUiState.Success(produtoEncontrado)
                        val selectableCriteria = SimilarityCriteriaList.getSelectableCriteriaForProduct(produtoEncontrado)
                        _availableCriteriaForSelection.value = selectableCriteria
                        _showSimilarityCriteriaSelectionButton.value = selectableCriteria.isNotEmpty()
                        findSimilarProducts(produtoEncontrado, currentSelectedKeys)
                    } else {
                        _uiState.value = DetalhesProdutoUiState.Error(Res.string.produto_nao_encontrado)
                        _showSimilarityCriteriaSelectionButton.value = false
                        _availableCriteriaForSelection.value = emptyList()
                    }
                },
                onFailure = {
                    _uiState.value = DetalhesProdutoUiState.Error(Res.string.erro_carregar_detalhes_produto)
                    _showSimilarityCriteriaSelectionButton.value = false
                    _availableCriteriaForSelection.value = emptyList()
                }
            )
        }
    }

    fun updateUserSelectedSimilarityCriteria(newKeys: Set<SimilarityCriterionKey>) {
        appViewModel.updateUserSelectedSimilarityCriteria(newKeys)
        (_uiState.value as? DetalhesProdutoUiState.Success)?.produto?.let { currentProduct ->
            viewModelScope.launch {
                findSimilarProducts(currentProduct, newKeys)
            }
        }
    }

    private fun getSignificantWords(name: String, productType: String): Set<String> {
        val commonStopWords = setOf(
            "de", "da", "do", "dos", "das", "para", "com", "sem", "a", "o", "e", "os", "as",
            "um", "uma", "uns", "umas", "ld", "le", "dir", "esq", "par", "un", "und", "unidade",
            "completo", "universal", "tipo", "modelo", "cor", "lado", "haste", "rosca", "lente",
            "h", "y", "es", "ks", "esd", "sport", "mix", "ate", "partir", "frente", "dianteiro",
            "traseiro", "novo", "nova", "original", "adapt", "anti", "reflexo", "c", "s",
            "frisada", "lisa", "furo", "anos", "ano", "todos", "todas", "em", "mm",
            "articulado", "fixo", "cromada", "prata", "preto", "vermelho", "azul", "brilh", "cristal", "laranja", "fume",
            "honda", "yamaha", "kawasaki", "bmw", "suzuki", "dafra", "kasinski"
        )

        val typeSpecificStopWords = when (productType.normalizeForSearch()) {
            "retrovisores" -> setOf("retrovisor")
            "mesas de guidao" -> setOf("cadeado", "mesa", "guidao", "mesinha")
            "piscas" -> setOf("pisca")
            "lentes" -> setOf("lente")
            "pedaleiras" -> setOf("pedaleira", "pedal")
            "manoplas" -> setOf("manopla")
            "borrachas" -> setOf("borracha")
            "suportes" -> setOf("suporte", "sup")
            "coxins" -> setOf("coxim", "kit")
            "carcacas" -> setOf("carcaca", "painel", "farol")
            "flanges" -> setOf("flange", "coletor")
            "roldanas" -> setOf("roldana")
            "suportes de placa" -> setOf("suporte", "placa", "padrao", "mercosul", "antigo")
            "expositores" -> setOf("expositor", "giratorio", "acrilico", "led")
            else -> emptySet()
        }

        val allStopWords = commonStopWords + typeSpecificStopWords

        val keepShortTerms = setOf(
            "cg", "cb", "pcx", "nxr", "xre", "nx", "xlr", "pop", "biz", "rd", "dt", "gs", "f", "z",
            "ex", "std", "abs", "cbs", "flex", "adv", "neo", "led", "mix", "es", "ks", "esd",
            "125", "150", "160", "190", "200", "250", "300", "400", "600", "650", "800", "1000", "1100", "1200",
            "09"
        )

        return name.normalizeForSearch()
            .split(Regex("[\\s-/]+"))
            .map { it.trim().replace(Regex("[^a-zA-Z0-9]"), "") }
            .filter { token ->
                (token.length > 2 || token.toIntOrNull() != null || token in keepShortTerms) && token.isNotBlank()
            }
            .filterNot { it in allStopWords }
            .toSet()
    }

    private fun extractBaseModelForRetrovisor(name: String): String {
        val normalizedName = name.normalizeForSearch()
        val words = normalizedName.split(Regex("\\s+"))
        val characteristicWords = setOf(
            "prata", "convexo", "anti", "reflexo", "articulado", "fixo", "cromada", "curta", "rebaixada",
            "ld", "le", "par", "(h)", "(y)", "haste", "lente", "rosca"
        )
        val modelWords = mutableListOf<String>()
        for (word in words) {
            if (word in characteristicWords || word.startsWith("(") || characteristicWords.any { charWord -> word.contains(charWord) && word != charWord }) {
                if (modelWords.isNotEmpty()) break
            }
            if (word.isNotBlank() && word != "retrovisor") {
                modelWords.add(word)
            }
        }
        if (modelWords.size > 1 && modelWords.first() == "mini" && modelWords[1] in setOf("titan", "bmw", "cb", "kawasaki", "redondo", "factor", "biz")) {
            return modelWords.take(3).joinToString(" ")
        }
        return modelWords.take(2).joinToString(" ").trim()
    }


    private suspend fun findSimilarProducts(currentProduct: Produto, criteriaKeysToCompare: Set<SimilarityCriterionKey>) {
        val allProductsResult = produtoDataSource.getProdutos()
        allProductsResult.getOrNull()?.let { allProducts ->

            _similarProdutos.value = allProducts.filter { otherProduct ->
                if (otherProduct.codigo == currentProduct.codigo) {
                    false
                } else if (otherProduct.tipo != currentProduct.tipo) {
                    false
                } else {
                    var criteriaFieldsMatch = true
                    if (criteriaKeysToCompare.contains(SimilarityCriterionKey.LENTE) && currentProduct.lente.isNotBlank()) {
                        if (otherProduct.lente != currentProduct.lente) criteriaFieldsMatch = false
                    }
                    if (criteriaFieldsMatch && criteriaKeysToCompare.contains(SimilarityCriterionKey.HASTE) && currentProduct.haste.isNotBlank()) {
                        if (otherProduct.haste != currentProduct.haste) criteriaFieldsMatch = false
                    }
                    if (criteriaFieldsMatch && criteriaKeysToCompare.contains(SimilarityCriterionKey.ROSCA) && currentProduct.rosca.isNotBlank()) {
                        if (otherProduct.rosca != currentProduct.rosca) criteriaFieldsMatch = false
                    }

                    var nameIsSimilar = false
                    if (criteriaFieldsMatch) {
                        if (currentProduct.tipo == "Retrovisores") {
                            val currentBaseModel = extractBaseModelForRetrovisor(currentProduct.nome)
                            val otherBaseModel = extractBaseModelForRetrovisor(otherProduct.nome)
                            if (currentBaseModel.isNotBlank() && otherBaseModel.isNotBlank()) {
                                nameIsSimilar = currentBaseModel == otherBaseModel
                                if (nameIsSimilar) {
                                    val currentNameLower = currentProduct.nome.normalizeForSearch()
                                    val otherNameLower = otherProduct.nome.normalizeForSearch()
                                    val hasArticuladoCurrent = currentNameLower.contains("articulado")
                                    val hasArticuladoOther = otherNameLower.contains("articulado")
                                    val hasHasteCurtaCurrent = currentNameLower.contains("haste curta")
                                    val hasHasteCurtaOther = otherNameLower.contains("haste curta")
                                    if (hasArticuladoCurrent != hasArticuladoOther) nameIsSimilar = false
                                    if (nameIsSimilar && hasHasteCurtaCurrent != hasHasteCurtaOther) nameIsSimilar = false
                                }
                            } else {
                                nameIsSimilar = false
                            }
                        } else {
                            val currentProductSignificantWords = getSignificantWords(currentProduct.nome, currentProduct.tipo)
                            val otherProductSignificantWords = getSignificantWords(otherProduct.nome, otherProduct.tipo)

                            if (currentProductSignificantWords.isEmpty() && otherProductSignificantWords.isEmpty()) {
                                nameIsSimilar = true
                            } else if (currentProductSignificantWords.isNotEmpty() && otherProductSignificantWords.isNotEmpty()) {
                                val intersection = currentProductSignificantWords.intersect(otherProductSignificantWords)
                                val union = currentProductSignificantWords.union(otherProductSignificantWords)
                                val jaccardIndex = if (union.isEmpty()) 0.0 else intersection.size.toDouble() / union.size.toDouble()

                                val minWordsForStricterCheck = 3
                                val requiredIntersectionSize = when (currentProduct.tipo) {
                                    "Mesas de Guidao", "Pedaleiras", "Suportes", "Carcacas", "Coxins" ->
                                        max(1, (min(currentProductSignificantWords.size, otherProductSignificantWords.size) * 0.6).toInt())
                                    else ->
                                        max(1, (min(currentProductSignificantWords.size, otherProductSignificantWords.size) * 0.5).toInt())
                                }

                                val jaccardThreshold = when (currentProduct.tipo) {
                                    "Mesas de Guidao", "Pedaleiras", "Suportes", "Carcacas", "Coxins" -> 0.40
                                    else -> 0.30
                                }

                                nameIsSimilar = if (currentProductSignificantWords.size >= minWordsForStricterCheck && otherProductSignificantWords.size >= minWordsForStricterCheck) {
                                    jaccardIndex >= jaccardThreshold && intersection.size >= requiredIntersectionSize
                                } else {
                                    jaccardIndex >= (jaccardThreshold - 0.05) && intersection.isNotEmpty()
                                }
                            } else {
                                nameIsSimilar = false
                            }
                        }
                    }
                    criteriaFieldsMatch && nameIsSimilar
                }
            }.sortedBy { it.nome }
        }
    }

    fun setIsProcessingPopBack(isProcessing: Boolean) {
        _isProcessingPopBack.value = isProcessing
    }
}