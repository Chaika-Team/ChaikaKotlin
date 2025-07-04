package com.example.chaika.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ProfileViewModel
import androidx.compose.runtime.*
import com.example.chaika.ui.screens.profile.views.PersonalDataView
import com.example.chaika.ui.screens.profile.views.AboutView
import com.example.chaika.ui.screens.profile.views.SettingsView
import com.example.chaika.ui.screens.profile.views.FaqsView
import com.example.chaika.ui.screens.profile.views.FeedbackView
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.screens.profile.views.MainProfileView

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    val conductor by viewModel.conductorState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    MainProfileView(
        viewModel = viewModel,
        authViewModel = authViewModel,
        conductor = conductor,
        navController = navController
    )
}
