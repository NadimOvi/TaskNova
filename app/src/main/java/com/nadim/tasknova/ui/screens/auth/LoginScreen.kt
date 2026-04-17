package com.nadim.tasknova.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

enum class LoginTab { EMAIL, PHONE }

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToPhone: (String) -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var selectedTab by remember { mutableStateOf(LoginTab.EMAIL) }

    // Email state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Phone state
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.OtpSent -> onNavigateToPhone(phone)
            else -> {}
        }
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
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo + Title
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn() + slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", fontSize = 52.sp, color = AriaGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text       = "TaskNova",
                        style      = MaterialTheme.typography.headlineLarge,
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text      = "Hi, I'm Aria. Your AI assistant.",
                        style     = MaterialTheme.typography.bodyLarge,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Google Sign In
            Button(
                onClick  = { /* Google — coming soon */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor   = Color.Black
                )
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Continue with Google",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // OR divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = TextHint)
                Text("  or  ", color = TextHint, fontSize = 13.sp)
                Divider(modifier = Modifier.weight(1f), color = TextHint)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab selector: Email / Phone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AriaSurface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text     = "Email",
                    selected = selectedTab == LoginTab.EMAIL,
                    onClick  = { selectedTab = LoginTab.EMAIL },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text     = "Phone",
                    selected = selectedTab == LoginTab.PHONE,
                    onClick  = { selectedTab = LoginTab.PHONE },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email login
            if (selectedTab == LoginTab.EMAIL) {
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Email") },
                    leadingIcon   = {
                        Icon(Icons.Default.Email, null, tint = AriaGreen)
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

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it },
                    label         = { Text("Password") },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = AriaGreen)
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
                                contentDescription = null,
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signInWithEmail(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AriaGreen,
                        contentColor   = Color.Black
                    ),
                    enabled = email.isNotBlank() &&
                            password.isNotBlank() &&
                            authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color    = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text       = "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }

            // Phone login
            if (selectedTab == LoginTab.PHONE) {
                OutlinedTextField(
                    value         = phone,
                    onValueChange = { phone = it },
                    label         = { Text("Phone number") },
                    placeholder   = { Text("+49 123 456 7890") },
                    leadingIcon   = {
                        Icon(Icons.Default.Phone, null, tint = AriaGreen)
                    },
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AriaGreen,
                        focusedLabelColor    = AriaGreen,
                        cursorColor          = AriaGreen,
                        unfocusedBorderColor = TextHint
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (phone.isNotBlank()) {
                            viewModel.signInWithPhone(phone)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AriaGreen,
                        contentColor   = Color.Black
                    ),
                    enabled = phone.isNotBlank() &&
                            authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color    = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text       = "Send OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }

            // Error
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text      = (authState as AuthState.Error).message,
                    color     = PriorityHigh,
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            TextButton(onClick = onNavigateToSignUp) {
                Text(
                    text  = "New here? Create account",
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(40.dp),
        shape    = RoundedCornerShape(10.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = if (selected) AriaGreen else Color.Transparent,
            contentColor   = if (selected) Color.Black else TextSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text       = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize   = 14.sp
        )
    }
}