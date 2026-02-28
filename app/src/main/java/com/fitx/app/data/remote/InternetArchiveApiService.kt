package com.fitx.app.data.remote

import com.fitx.app.data.remote.dto.InternetArchiveMetadataResponse
import com.fitx.app.data.remote.dto.InternetArchiveSearchResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface InternetArchiveApiService {
    @GET
    suspend fun searchByUrl(
        @Url url: String
    ): InternetArchiveSearchResponse

    @GET
    suspend fun fetchMetadataByUrl(
        @Url url: String
    ): InternetArchiveMetadataResponse
}
