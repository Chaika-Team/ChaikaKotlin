package com.chaikasoft.app.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.util.Log
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import androidx.compose.ui.res.stringResource
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.theme.LoginDimens
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.ui.viewModels.AuthState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val isBusy = ui.state is AuthState.Checking

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginScreen", "ActivityResultLauncher callback: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.handleAuthResult(intent)
            } ?: Log.w("LoginScreen", "RESULT_OK but intent is null")
        } else {
            Log.w("LoginScreen", "Result code != OK: ${result.resultCode}")
        }
    }

    // В auth-графе Back закрывает приложение
    BackHandler {
        (ctx as? Activity)?.finishAffinity()
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
            enabled = !isBusy
        ) {
            Text(stringResource(R.string.login_button))
        }

        if (isBusy) {
            CircularProgressIndicator(
                modifier = Modifier.padding(LoginDimens.LoadingIndicatorPadding)
            )
        }

        ui.errorMessage?.let { error ->
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
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text(stringResource(R.string.login_ok))
                    }
                }
            }
        }
    }
}
