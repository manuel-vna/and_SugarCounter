package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup

class FilterEntriesBySearchFieldUseCase {

    operator fun invoke(searchFieldText: String, entryList: List<EntryGroup>): List<EntryGroup> {
        return entryList.filter { entryGroup ->
            entryGroup.entryList.any { entry ->
                entry.category.lowercase().contains(searchFieldText.lowercase())
            }
        }
    }

}