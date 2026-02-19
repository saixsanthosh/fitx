package com.fitx.app.data.remote.dto

data class FoodSearchRequest(
    val query: String,
    val pageSize: Int = 20,
    val pageNumber: Int = 1
)

