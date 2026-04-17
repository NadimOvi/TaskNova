package com.nadim.tasknova.data.model

data class UserProfile(
    val id: String,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val phone: String? = null,
    val theme: String = "dark",
    val accentColor: String = "#00C97B",
    val createdAt: String? = null
)