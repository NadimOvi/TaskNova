package com.nadim.tasknova.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.model.UserProfile
import com.nadim.tasknova.data.preferences.UserPreferences
import com.nadim.tasknova.repository.UserRepository
import com.nadim.tasknova.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val theme = userPreferences.theme
    val accentColor = userPreferences.accentColor

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            _profile.value = userRepository.getProfile(userId)
        }
    }

    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(fullName = name, phone = phone)
            userRepository.updateProfile(updated)
            userPreferences.setUserName(name)
            _profile.value = updated
        }
    }

    fun uploadAvatar(imageBytes: ByteArray) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.currentUserId ?: return@launch
            val url = userRepository.uploadAvatar(userId, imageBytes)
            if (url != null) {
                val current = _profile.value ?: return@launch
                val updated = current.copy(avatarUrl = url)
                userRepository.updateProfile(updated)
                userPreferences.setAvatarUrl(url)
                _profile.value = updated
            }
            _isLoading.value = false
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            userPreferences.setTheme(theme)
            val current = _profile.value ?: return@launch
            userRepository.updateProfile(current.copy(theme = theme))
        }
    }

    fun setAccentColor(color: String) {
        viewModelScope.launch {
            userPreferences.setAccentColor(color)
            val current = _profile.value ?: return@launch
            userRepository.updateProfile(current.copy(accentColor = color))
        }
    }
}