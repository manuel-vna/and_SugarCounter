package com.jumparoundcreations.mva_sugarcounter.api

import com.jumparoundcreations.mva_sugarcounter.api.data.ProductResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface FoodApiService {
    suspend fun getProductByBarcode(barcode: String): Result<ProductResponse>
}

class FoodApiServiceImpl(private val client: HttpClient) : FoodApiService {
    override suspend fun getProductByBarcode(barcode: String): Result<ProductResponse> {
        return try {
            val response: ProductResponse = client.get("https://world.openfoodfacts.net/api/v2/product/$barcode").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
