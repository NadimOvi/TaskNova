package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.UserProfile
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUserOrNull()?.id

    override val isLoggedIn: Boolean
        get() = auth.currentUserOrNull() != null

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> {
        return try {
            auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
            }
            val user = auth.currentUserOrNull()
            Result.success(
                UserProfile(
                    id        = user?.id ?: "",
                    fullName  = user?.userMetadata
                        ?.get("full_name")?.toString()?.trim('"'),
                    avatarUrl = user?.userMetadata
                        ?.get("avatar_url")?.toString()?.trim('"')
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithPhone(phone: String): Result<Unit> {
        return try {
            auth.signInWith(Phone) {
                this.phone = phone
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<UserProfile> {
        return try {
            auth.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phone = phone,
                token = otp
            )
            val user = auth.currentUserOrNull()
            Result.success(UserProfile(id = user?.id ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): Flow<UserProfile?> = flow {
        val user = auth.currentUserOrNull()
        if (user != null) {
            emit(
                UserProfile(
                    id        = user.id,
                    fullName  = user.userMetadata
                        ?.get("full_name")?.toString()?.trim('"'),
                    avatarUrl = user.userMetadata
                        ?.get("avatar_url")?.toString()?.trim('"')
                )
            )
        } else {
            emit(null)
        }
    }
}