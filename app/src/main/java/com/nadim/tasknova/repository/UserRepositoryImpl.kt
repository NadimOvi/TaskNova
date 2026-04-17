package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.UserProfile
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : UserRepository {

    override suspend fun getProfile(userId: String): UserProfile? {
        return try {
            val result = postgrest
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Map<String, String?>>()
            result?.let {
                UserProfile(
                    id          = it["id"] ?: "",
                    fullName    = it["full_name"],
                    avatarUrl   = it["avatar_url"],
                    phone       = it["phone"],
                    theme       = it["theme"] ?: "dark",
                    accentColor = it["accent_color"] ?: "#00C97B"
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        try {
            postgrest.from("profiles").update(
                mapOf(
                    "full_name"    to profile.fullName,
                    "avatar_url"   to profile.avatarUrl,
                    "phone"        to profile.phone,
                    "theme"        to profile.theme,
                    "accent_color" to profile.accentColor
                )
            ) {
                filter {
                    eq("id", profile.id)
                }
            }
        } catch (e: Exception) { }
    }

    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray
    ): String? {
        return try {
            val path = "$userId/avatar.jpg"
            storage.from("avatars").upload(
                path = path,
                data = imageBytes,
                upsert = true
            )
            storage.from("avatars").publicUrl(path)
        } catch (e: Exception) {
            null
        }
    }
}