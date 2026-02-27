package com.fitx.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VersionInfoDto(
    @SerializedName(value = "latestVersion", alternate = ["version", "latest"])
    val latestVersion: String,
    @SerializedName(value = "message", alternate = ["notes", "changelog"])
    val message: String? = null,
    @SerializedName(value = "downloadUrl", alternate = ["apkUrl", "url"])
    val downloadUrl: String? = null
)
