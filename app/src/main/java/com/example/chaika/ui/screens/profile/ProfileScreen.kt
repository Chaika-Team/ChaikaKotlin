package com.example.chaika.ui.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.chaika.ui.components.profile.UserHeaderSection
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ProfileViewModel
import androidx.compose.runtime.*
import com.example.chaika.ui.screens.profile.views.PersonalDataView
import com.example.chaika.ui.screens.profile.views.AboutView
import com.example.chaika.ui.screens.profile.views.SettingsView
import com.example.chaika.ui.screens.profile.views.FaqsView
import com.example.chaika.ui.screens.profile.views.FeedbackView
import com.example.chaika.ui.viewModels.AuthViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.screens.profile.views.MainProfileView
import com.example.chaika.ui.theme.ProfileDimens
import com.example.chaika.ui.theme.ProfileBackground

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val conductor by viewModel.conductorState.collectAsState()

    LaunchedEffect(uiState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val targetRoute = when (uiState) {
            is ProfileViewModel.ScreenState.Profile -> Routes.PROFILE
            is ProfileViewModel.ScreenState.PersonalData -> Routes.PROFILE_PERSONAL_DATA
            is ProfileViewModel.ScreenState.Settings -> Routes.PROFILE_SETTINGS
            is ProfileViewModel.ScreenState.Faqs -> Routes.PROFILE_FAQS
            is ProfileViewModel.ScreenState.Feedback -> Routes.PROFILE_FEEDBACK
            is ProfileViewModel.ScreenState.About -> Routes.PROFILE_ABOUT
        }
        if (targetRoute != currentRoute) {
            navController.navigate(targetRoute) {
                popUpTo(Routes.PROFILE_GRAPH) { inclusive = false }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileBackground)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (uiState is ProfileViewModel.ScreenState.Profile) {
                    UserHeaderSection(conductor = conductor, onClick = { /* TODO: handle add user */ })
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(
                                topStart = if (uiState is ProfileViewModel.ScreenState.Profile) ProfileDimens.ProfileCardCornerRadius else 0.dp, 
                                topEnd = if (uiState is ProfileViewModel.ScreenState.Profile) ProfileDimens.ProfileCardCornerRadius else 0.dp
                            )
                        )
                ) {
                    when (uiState) {
                        is ProfileViewModel.ScreenState.Profile -> {
                            MainProfileView(
                                viewModel = viewModel,
                                authViewModel = authViewModel,
                                conductor = conductor
                            )
                        }
                        is ProfileViewModel.ScreenState.PersonalData -> {
                            PersonalDataView(conductor = conductor)
                        }
                        is ProfileViewModel.ScreenState.Settings -> {
                            SettingsView()
                        }
                        is ProfileViewModel.ScreenState.Faqs -> {
                            FaqsView()
                        }
                        is ProfileViewModel.ScreenState.Feedback -> {
                            FeedbackView()
                        }
                        is ProfileViewModel.ScreenState.About -> {
                            AboutView()
                        }
                    }
                }
            }
        }
    }
}
