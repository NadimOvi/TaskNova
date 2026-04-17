package com.nadim.tasknova.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "tasknova_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_THEME      = stringPreferencesKey("theme")
        val KEY_ACCENT     = stringPreferencesKey("accent_color")
        val KEY_USER_ID    = stringPreferencesKey("user_id")
        val KEY_ONBOARDED  = booleanPreferencesKey("onboarded")
        val KEY_USER_NAME  = stringPreferencesKey("user_name")
        val KEY_AVATAR_URL = stringPreferencesKey("avatar_url")
    }

    val theme: Flow<String> = context.dataStore.data
        .map { it[KEY_THEME] ?: "dark" }

    val accentColor: Flow<String> = context.dataStore.data
        .map { it[KEY_ACCENT] ?: "#00C97B" }

    val isOnboarded: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_ONBOARDED] ?: false }

    val userName: Flow<String> = context.dataStore.data
        .map { it[KEY_USER_NAME] ?: "" }

    val avatarUrl: Flow<String> = context.dataStore.data
        .map { it[KEY_AVATAR_URL] ?: "" }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun setAccentColor(color: String) {
        context.dataStore.edit { it[KEY_ACCENT] = color }
    }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDED] = value }
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { it[KEY_USER_ID] = id }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[KEY_USER_NAME] = name }
    }

    suspend fun setAvatarUrl(url: String) {
        context.dataStore.edit { it[KEY_AVATAR_URL] = url }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}