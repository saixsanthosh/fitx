package com.fitx.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YouTubePlaylistListResponse(
    @SerializedName("items") val items: List<YouTubePlaylistDto> = emptyList()
)

data class YouTubePlaylistDto(
    @SerializedName("id") val id: String,
    @SerializedName("snippet") val snippet: YouTubeSnippetDto? = null,
    @SerializedName("contentDetails") val contentDetails: YouTubeContentDetailsDto? = null
)

data class YouTubeSnippetDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("channelTitle") val channelTitle: String? = null,
    @SerializedName("thumbnails") val thumbnails: YouTubeThumbnailsDto? = null
)

data class YouTubeContentDetailsDto(
    @SerializedName("itemCount") val itemCount: Int? = null
)

data class YouTubeThumbnailsDto(
    @SerializedName("default") val defaultThumb: YouTubeThumbnailDto? = null,
    @SerializedName("medium") val mediumThumb: YouTubeThumbnailDto? = null,
    @SerializedName("high") val highThumb: YouTubeThumbnailDto? = null
)

data class YouTubeThumbnailDto(
    @SerializedName("url") val url: String? = null
)
