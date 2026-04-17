package com.nadim.tasknova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nadim.tasknova.ui.screens.auth.LoginScreen
import com.nadim.tasknova.ui.screens.auth.PhoneOtpScreen
import com.nadim.tasknova.ui.screens.auth.SignUpScreen
import com.nadim.tasknova.ui.screens.expenses.ExpensesScreen
import com.nadim.tasknova.ui.screens.home.HomeScreen
import com.nadim.tasknova.ui.screens.notes.NotesScreen
import com.nadim.tasknova.ui.screens.profile.ProfileScreen
import com.nadim.tasknova.ui.screens.reminders.RemindersScreen
import com.nadim.tasknova.ui.screens.tasks.TasksScreen
import com.nadim.tasknova.ui.screens.voice.VoiceScreen
import com.nadim.tasknova.viewmodel.AuthViewModel

object Routes {
    const val LOGIN       = "login"
    const val SIGNUP      = "signup"
    const val PHONE_OTP   = "phone_otp/{phone}"
    const val HOME        = "home"
    const val VOICE       = "voice"
    const val TASKS       = "tasks"
    const val REMINDERS   = "reminders"
    const val NOTES       = "notes"
    const val EXPENSES    = "expenses"
    const val PROFILE     = "profile"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val startDest = if (authViewModel.isLoggedIn) Routes.HOME else Routes.LOGIN

    NavHost(
        navController  = navController,
        startDestination = startDest
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToSignUp  = { navController.navigate(Routes.SIGNUP) },
                onNavigateToPhone   = { navController.navigate(Routes.PHONE_OTP.replace("{phone}", it)) },
                onLoginSuccess      = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                onNavigateBack  = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.PHONE_OTP) { backStack ->
            val phone = backStack.arguments?.getString("phone") ?: ""
            PhoneOtpScreen(
                phone           = phone,
                onVerifySuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack  = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToVoice    = { navController.navigate(Routes.VOICE) },
                onNavigateToTasks    = { navController.navigate(Routes.TASKS) },
                onNavigateToReminders = { navController.navigate(Routes.REMINDERS) },
                onNavigateToNotes    = { navController.navigate(Routes.NOTES) },
                onNavigateToExpenses = { navController.navigate(Routes.EXPENSES) },
                onNavigateToProfile  = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.VOICE) {
            VoiceScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.TASKS) {
            TasksScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.REMINDERS) {
            RemindersScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.NOTES) {
            NotesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.EXPENSES) {
            ExpensesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}