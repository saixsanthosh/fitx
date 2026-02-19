package com.fitx.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodSearchResponse(
    @SerializedName("foods") val foods: List<FoodDto> = emptyList()
)

data class FoodDto(
    @SerializedName("fdcId") val fdcId: Long,
    @SerializedName("description") val description: String,
    @SerializedName("foodNutrients") val foodNutrients: List<FoodNutrientDto> = emptyList(),
    @SerializedName("servingSize") val servingSize: Double? = null,
    @SerializedName("servingSizeUnit") val servingSizeUnit: String? = null
)

data class FoodNutrientDto(
    @SerializedName("nutrientName") val nutrientName: String,
    @SerializedName("value") val value: Double?
)
