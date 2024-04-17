package com.example.mva_sugarcounter.data.states

import com.example.mva_sugarcounter.data.Category

data class CategoryListing(
    val displayed: Boolean = false,
    val categories: Map<Char,List<Category>>
)
