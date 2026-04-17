package com.nadim.tasknova.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.AuthState
import com.nadim.tasknova.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onSignUpSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AriaDark, AriaSurface))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            IconButton(
                onClick  = onNavigateBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Create account",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Sign up to get started with Aria",
                style     = MaterialTheme.typography.bodyMedium,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                label         = { Text("Email") },
                leadingIcon   = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = AriaGreen
                    )
                },
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AriaGreen,
                    focusedLabelColor    = AriaGreen,
                    cursorColor          = AriaGreen,
                    unfocusedBorderColor = TextHint
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                label         = { Text("Password") },
                leadingIcon   = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = AriaGreen
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = "Toggle",
                            tint = TextHint
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AriaGreen,
                    focusedLabelColor    = AriaGreen,
                    cursorColor          = AriaGreen,
                    unfocusedBorderColor = TextHint
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password
            OutlinedTextField(
                value         = confirmPassword,
                onValueChange = { confirmPassword = it },
                label         = { Text("Confirm password") },
                leadingIcon   = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = AriaGreen
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AriaGreen,
                    focusedLabelColor    = AriaGreen,
                    cursorColor          = AriaGreen,
                    unfocusedBorderColor = TextHint
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign up button
            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            passwordError = "Email is required"
                        }
                        password.length < 6 -> {
                            passwordError = "Password must be at least 6 characters"
                        }
                        password != confirmPassword -> {
                            passwordError = "Passwords do not match"
                        }
                        else -> {
                            passwordError = ""
                            viewModel.signUpWithEmail(email, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AriaGreen,
                    contentColor   = Color.Black
                ),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color    = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text       = "Create Account",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            // Validation error
            if (passwordError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text  = passwordError,
                    color = PriorityHigh,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // API error
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text      = (authState as AuthState.Error).message,
                    color     = PriorityHigh,
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateBack) {
                Text(
                    text  = "Already have an account? Sign in",
                    color = TextSecondary
                )
            }
        }
    }
}