package com.fitx.app.data.remote

import com.fitx.app.data.remote.dto.VersionInfoDto
import retrofit2.http.GET
import retrofit2.http.Url

interface AppUpdateApiService {
    @GET
    suspend fun fetchVersionInfo(@Url url: String): VersionInfoDto
}
