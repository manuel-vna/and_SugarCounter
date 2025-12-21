package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.EntryGroup

class FilterEntriesBySearchFieldUseCase {

    operator fun invoke(searchFieldText: String, entryList: List<EntryGroup>): List<EntryGroup> {
        return entryList.filter { entryGroup ->
            entryGroup.entryList.any { entry ->
                entry.category.lowercase().contains(searchFieldText.lowercase())
            }
        }
    }

}