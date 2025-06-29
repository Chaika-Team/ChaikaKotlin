package com.example.chaika.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import android.util.Log
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.AuthViewModel
import androidx.compose.ui.res.stringResource
import com.example.chaika.R
import com.example.chaika.ui.theme.LoginDimens
import androidx.activity.compose.BackHandler
import kotlin.system.exitProcess

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginScreen", "ActivityResultLauncher callback triggered")
        Log.d("LoginScreen", "Result code: ${result.resultCode}")

        if (result.resultCode == android.app.Activity.RESULT_OK) {
            Log.d("LoginScreen", "Result OK - processing auth result")
            result.data?.let { intent ->
                viewModel.handleAuthResult(intent)
            } ?: run {
                Log.w("LoginScreen", "Result OK but intent data is null")
            }
        } else {
            Log.w("LoginScreen", "Result code is not RESULT_OK: ${result.resultCode}")
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            Log.d("LoginScreen", "isAuthenticated = true, navigating to TRIP_GRAPH")
            navController.navigate(Routes.TRIP_GRAPH) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.onNavigationHandled()
        }
    }

    if (uiState.isCheckingAuth) {
        Log.d("LoginScreen", "Showing initial loading screen")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = stringResource(R.string.exit_confirmation_title)) },
            text = { Text(text = stringResource(R.string.exit_confirmation_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    exitProcess(0)
                }) {
                    Text(text = stringResource(R.string.exit_confirmation_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = stringResource(R.string.exit_confirmation_no))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LoginDimens.LoginContainerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                Log.d("LoginScreen", "Login button clicked")
                val authIntent = viewModel.startAuth()
                authLauncher.launch(authIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(stringResource(R.string.login_button))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(LoginDimens.LoadingIndicatorPadding)
            )
        }

        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LoginDimens.ErrorCardTopPadding),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LoginDimens.ErrorCardPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text(stringResource(R.string.login_ok))
                    }
                }
            }
        }
    }
}