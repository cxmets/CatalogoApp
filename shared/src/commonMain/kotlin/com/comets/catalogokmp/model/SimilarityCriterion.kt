package com.comets.catalogokmp.model

import com.comets.catalogokmp.data.model.Produto
import org.jetbrains.compose.resources.StringResource
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.similar_criteria_lente
import catalogokmp.shared.generated.resources.similar_criteria_haste
import catalogokmp.shared.generated.resources.similar_criteria_rosca

enum class SimilarityCriterionKey {
    LENTE, HASTE, ROSCA
}

data class SelectableSimilarityCriterion(
    val key: SimilarityCriterionKey,
    val displayNameResource: StringResource,
    val isPresent: Boolean
)

object SimilarityCriteriaList {
    fun getSelectableCriteriaForProduct(produto: Produto): List<SelectableSimilarityCriterion> {
        return listOfNotNull(
            if (produto.lente.isNotBlank()) SelectableSimilarityCriterion(SimilarityCriterionKey.LENTE, Res.string.similar_criteria_lente, true) else null,
            if (produto.haste.isNotBlank()) SelectableSimilarityCriterion(SimilarityCriterionKey.HASTE, Res.string.similar_criteria_haste, true) else null,
            if (produto.rosca.isNotBlank()) SelectableSimilarityCriterion(SimilarityCriterionKey.ROSCA, Res.string.similar_criteria_rosca, true) else null
        ).filter { it.isPresent }
    }
}