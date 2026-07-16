package com.jumparoundcreations.mva_sugarcounter.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("product") val product: Product? = null,
    @SerialName("status") val status: Int,
    @SerialName("status_verbose") val statusVerbose: String? = null
)

@Serializable
data class Product(
    @SerialName("product_name") val productName: String? = null,
    @SerialName("serving_quantity") val servingQuantity: Double? = null,
    @SerialName("nutriments") val nutriments: Nutriments? = null,
    @SerialName("code") val code: String? = null
)

@Serializable
data class Nutriments(
    @SerialName("sugars_100g") val sugars100g: Double? = null,
    @SerialName("sugars_serving") val sugarsServing: Double? = null,
    @SerialName("sugars_unit") val sugarsUnit: String? = null
)
