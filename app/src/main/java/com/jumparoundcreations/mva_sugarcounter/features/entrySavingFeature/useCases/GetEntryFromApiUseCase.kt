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
                    val serving_quantity = productResponse.product?.servingQuantity
                    val sugars_serving = productResponse.product?.nutriments?.sugarsServing
                    val sugars_100g = productResponse.product?.nutriments?.sugars100g
                    println("Product Response: $productResponse")

                    if (category.isNullOrEmpty()) {
                        return GetEntryByApiResult.ProductNotFound
                    }

                    if (serving_quantity != null && sugars_serving != null && sugars_100g != sugars_serving){
                        return GetEntryByApiResult.ProductFound(
                            entryType = GramCountMode.PerPiece,
                            category = category,
                            gram = sugars_serving
                        )
                    } else if ( sugars_100g != null ) {
                        return GetEntryByApiResult.ProductFound(
                            entryType = GramCountMode.PerHundred,
                            category = category,
                            gram = sugars_100g
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
