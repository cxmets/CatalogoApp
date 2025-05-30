package com.comets.catalogokmp.model

import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.sort_by_code_asc
import catalogokmp.shared.generated.resources.sort_by_code_desc
import catalogokmp.shared.generated.resources.sort_by_name_asc
import catalogokmp.shared.generated.resources.sort_by_name_desc
import catalogokmp.shared.generated.resources.sort_by_type_asc
import catalogokmp.shared.generated.resources.sort_by_type_desc
import org.jetbrains.compose.resources.StringResource

enum class SortCriteria {
    NAME,
    CODE,
    TYPE
}

enum class SortDirection {
    ASC,
    DESC
}

data class SortOption(
    val criteria: SortCriteria,
    val direction: SortDirection,
    val displayNameResource: StringResource
) {
    companion object {
        val NAME_ASC by lazy { SortOption(SortCriteria.NAME, SortDirection.ASC, Res.string.sort_by_name_asc) }
        val NAME_DESC by lazy { SortOption(SortCriteria.NAME, SortDirection.DESC, Res.string.sort_by_name_desc) }
        val CODE_ASC by lazy { SortOption(SortCriteria.CODE, SortDirection.ASC, Res.string.sort_by_code_asc) }
        val CODE_DESC by lazy { SortOption(SortCriteria.CODE, SortDirection.DESC, Res.string.sort_by_code_desc) }
        val TYPE_ASC by lazy { SortOption(SortCriteria.TYPE, SortDirection.ASC, Res.string.sort_by_type_asc) }
        val TYPE_DESC by lazy { SortOption(SortCriteria.TYPE, SortDirection.DESC, Res.string.sort_by_type_desc) }

        val DEFAULT = NAME_ASC // Mantendo o padrão

        val allOptions: List<SortOption> by lazy {
            listOf(
                NAME_ASC,
                NAME_DESC,
                CODE_ASC,
                CODE_DESC,
                TYPE_ASC,
                TYPE_DESC
            )
        }
    }
}