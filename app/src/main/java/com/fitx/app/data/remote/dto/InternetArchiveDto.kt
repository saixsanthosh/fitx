package com.fitx.app.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class InternetArchiveSearchResponse(
    @SerializedName("response") val response: InternetArchiveSearchInner = InternetArchiveSearchInner()
)

data class InternetArchiveSearchInner(
    @SerializedName("docs") val docs: List<InternetArchiveDocDto> = emptyList()
)

data class InternetArchiveDocDto(
    @SerializedName("identifier") val identifier: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("creator") val creator: JsonElement? = null
)

data class InternetArchiveMetadataResponse(
    @SerializedName("files") val files: List<InternetArchiveFileDto> = emptyList()
)

data class InternetArchiveFileDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("format") val format: String? = null,
    @SerializedName("length") val length: String? = null
)
