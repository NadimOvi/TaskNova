package com.nadim.tasknova.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.AuthViewModel
import com.nadim.tasknova.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profile.collectAsState()
    val theme   by profileViewModel.theme.collectAsState(initial = "dark")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text       = "Profile",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AriaSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = profile?.fullName?.first()?.toString() ?: "A",
                    color      = AriaGreen,
                    fontSize   = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = profile?.fullName ?: "User",
                color      = TextPrimary,
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = profile?.phone ?: "",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Theme toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AriaCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (theme == "dark") Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme",
                            tint     = AriaGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text  = if (theme == "dark") "Dark mode" else "Light mode",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked         = theme == "dark",
                        onCheckedChange = {
                            profileViewModel.setTheme(if (it) "dark" else "light")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = Color.Black,
                            checkedTrackColor  = AriaGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign out
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AriaCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick  = { authViewModel.signOut() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Sign out",
                            tint = PriorityHigh
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign out", color = PriorityHigh, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}