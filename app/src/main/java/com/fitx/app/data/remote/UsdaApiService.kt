package com.fitx.app.data.remote

import com.fitx.app.data.remote.dto.FoodSearchRequest
import com.fitx.app.data.remote.dto.FoodSearchResponse
import com.fitx.app.data.remote.dto.FoodDto
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UsdaApiService {
    @POST("foods/search")
    suspend fun searchFoods(
        @Query("api_key") apiKey: String,
        @Body request: FoodSearchRequest
    ): FoodSearchResponse

    @GET("foods/list")
    suspend fun listFoods(
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 200,
        @Query("pageNumber") pageNumber: Int = 1
    ): List<FoodDto>
}
