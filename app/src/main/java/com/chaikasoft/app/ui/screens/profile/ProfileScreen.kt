package com.chaikasoft.app.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.ui.screens.profile.views.MainProfileView
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import com.chaikasoft.app.ui.viewModels.ConductorViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    conductorViewModel: ConductorViewModel,
    authViewModel: AuthViewModel
) {
    // Профиль просто рисует данные. Никакой навигации по isAuthenticated здесь больше нет.
    val conductor by conductorViewModel.conductor.collectAsStateWithLifecycle()

    MainProfileView(
        authViewModel = authViewModel,
        conductor = conductor,
        navController = navController
    )
}
