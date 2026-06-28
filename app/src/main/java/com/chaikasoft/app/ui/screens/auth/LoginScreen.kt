package com.chaikasoft.app.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.LoginDimens
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.AuthState
import com.chaikasoft.app.ui.viewmodels.AuthUiState
import com.chaikasoft.app.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

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

    LoginContent(
        ui = ui,
        onLoginClick = {
            Log.d("LoginScreen", "Login button clicked")
            val authIntent = viewModel.startAuth()
            authLauncher.launch(authIntent)
        },
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginContent(
    ui: AuthUiState,
    onLoginClick: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBusy = ui.state is AuthState.Checking

    Column(
        modifier = Modifier
            .then(modifier)
            .testTag("loginScreen")
            .semantics { testTagsAsResourceId = true }
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LoginDimens.LoginContainerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("loginButton"),
            enabled = !isBusy
        ) {
            Text(stringResource(R.string.login_button))
        }

        if (isBusy) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(LoginDimens.LoadingIndicatorPadding)
                    .testTag("loginProgress")
            )
        }

        ui.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LoginDimens.ErrorCardTopPadding)
                    .testTag("loginErrorCard"),
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
                    TextButton(onClick = onClearError) {
                        Text(stringResource(R.string.login_ok))
                    }
                }
            }
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun LoginContentPreview() {
    ChaikaTheme {
        LoginContent(
            ui = AuthUiState(
                state = AuthState.Unauthenticated,
                errorMessage = "Не удалось завершить вход. Проверьте соединение."
            ),
            onLoginClick = {},
            onClearError = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun LoginContentWidePreview() {
    ChaikaTheme {
        LoginContent(
            ui = AuthUiState(state = AuthState.Checking),
            onLoginClick = {},
            onClearError = {}
        )
    }
}
