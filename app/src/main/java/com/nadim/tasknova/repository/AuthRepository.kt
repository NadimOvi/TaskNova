package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isLoggedIn: Boolean
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signInWithPhone(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<UserProfile>
    suspend fun signOut()
    fun getCurrentUser(): Flow<UserProfile?>
}