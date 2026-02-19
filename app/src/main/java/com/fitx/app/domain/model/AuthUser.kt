package com.fitx.app.domain.model

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

