package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getProfile(userId: String): UserProfile?
    suspend fun updateProfile(profile: UserProfile)
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String?
}