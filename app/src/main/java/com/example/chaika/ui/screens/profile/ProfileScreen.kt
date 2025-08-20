package com.example.chaika.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.chaika.ui.navigation.Routes
import androidx.compose.runtime.*
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.screens.profile.views.MainProfileView
import com.example.chaika.ui.viewModels.ConductorViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    conductorViewModel: ConductorViewModel,
    authViewModel: AuthViewModel
) {
    val conductor by conductorViewModel.conductor.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    MainProfileView(
        authViewModel = authViewModel,
        conductor = conductor,
        navController = navController
    )
}
