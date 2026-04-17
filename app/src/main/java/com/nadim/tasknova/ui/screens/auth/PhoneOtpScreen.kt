package com.nadim.tasknova.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.AuthState
import com.nadim.tasknova.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneOtpScreen(
    phone: String,
    onVerifySuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var otp by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onVerifySuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text       = "Verify your number",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "We sent a code to $phone",
                style     = MaterialTheme.typography.bodyMedium,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value         = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label         = { Text("Enter OTP") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AriaGreen,
                    focusedLabelColor    = AriaGreen,
                    cursorColor          = AriaGreen,
                    unfocusedBorderColor = TextHint
                ),
                singleLine = true,
                textStyle  = LocalTextStyle.current.copy(
                    fontSize  = 24.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick  = {
                    if (otp.length == 6) viewModel.verifyOtp(phone, otp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AriaGreen,
                    contentColor   = Color.Black
                ),
                enabled = otp.length == 6 && authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color    = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text       = "Verify",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text  = (authState as AuthState.Error).message,
                    color = PriorityHigh
                )
            }
        }
    }
}