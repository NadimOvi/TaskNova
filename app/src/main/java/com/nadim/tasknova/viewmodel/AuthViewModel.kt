package com.nadim.tasknova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.model.UserProfile
import com.nadim.tasknova.data.preferences.UserPreferences
import com.nadim.tasknova.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val profile: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
    object OtpSent : AuthState()
    object LoggedOut : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { profile ->
                    userPreferences.setUserId(profile.id)
                    userPreferences.setUserName(profile.fullName ?: "")
                    userPreferences.setAvatarUrl(profile.avatarUrl ?: "")
                    userPreferences.setOnboarded(true)
                    _authState.value = AuthState.Success(profile)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Sign in failed")
                }
            )
        }
    }

    fun signInWithPhone(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithPhone(phone)
            result.fold(
                onSuccess = { _authState.value = AuthState.OtpSent },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Failed to send OTP")
                }
            )
        }
    }

    fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.verifyOtp(phone, otp)
            result.fold(
                onSuccess = { profile ->
                    userPreferences.setUserId(profile.id)
                    userPreferences.setOnboarded(true)
                    _authState.value = AuthState.Success(profile)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "OTP verification failed")
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            userPreferences.clear()
            _authState.value = AuthState.LoggedOut
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}