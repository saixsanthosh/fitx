package com.fitx.app.data.remote

import com.fitx.app.data.remote.dto.YouTubePlaylistListResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface YouTubeApiService {
    @GET
    suspend fun fetchPlaylistsByUrl(
        @Url url: String
    ): YouTubePlaylistListResponse
}
