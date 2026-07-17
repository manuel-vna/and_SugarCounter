package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.api.FoodApiService
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GetEntryByApiResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

class GetEntryFromApiUseCase(
    private val foodApiService: FoodApiService,
) {

    suspend operator fun invoke(barcode: String): GetEntryByApiResult {
            foodApiService.getProductByBarcode(barcode)
                .onSuccess { productResponse ->

                    val category = productResponse.product?.productName
                    val servingQuantity = productResponse.product?.servingQuantity
                    val sugarsServing = productResponse.product?.nutriments?.sugarsServing
                    val sugars100g = productResponse.product?.nutriments?.sugars100g
                    println("Product Response: $productResponse")

                    if (category.isNullOrEmpty()) {
                        return GetEntryByApiResult.ProductNotFound
                    }

                    if (servingQuantity != null && sugarsServing != null && sugars100g != sugarsServing){
                        return GetEntryByApiResult.ProductFound(
                            entryType = GramCountMode.PerPiece,
                            category = category,
                            gramPerHundred = sugars100g,
                            gramPerPiece = sugarsServing
                        )
                    } else if ( sugars100g != null ) {
                        return GetEntryByApiResult.ProductFound(
                            entryType = GramCountMode.PerHundred,
                            category = category,
                            gramPerHundred = sugars100g,
                            gramPerPiece = sugarsServing
                        )
                    } else {
                        return GetEntryByApiResult.ProductNotFound
                    }

                }
                .onFailure { error ->
                    println("Failed to fetch product: ${error.message}")
                    return GetEntryByApiResult.ProductNotFound
                }
        return GetEntryByApiResult.ProductNotFound
    }

}
