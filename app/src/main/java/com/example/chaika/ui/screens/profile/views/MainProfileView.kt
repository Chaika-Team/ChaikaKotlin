package com.example.chaika.ui.screens.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.example.chaika.R
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.ui.components.profile.ProfileMenuItem
import com.example.chaika.ui.components.profile.ProfileMenuItemShape
import com.example.chaika.ui.components.profile.SectionSpacer
import com.example.chaika.ui.components.profile.UserHeaderSection
import com.example.chaika.ui.components.profile.UserInfoSection
import com.example.chaika.ui.theme.ProfileBackground
import com.example.chaika.ui.theme.ProfileDimens
import com.example.chaika.ui.viewModels.AuthViewModel
import androidx.navigation.NavHostController
import com.example.chaika.ui.navigation.Routes

@Composable
fun MainProfileView(
    authViewModel: AuthViewModel,
    conductor: ConductorDomain?,
    navController: NavHostController
) {
    val uiState by authViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserHeaderSection(conductor = conductor, onClick = { /* TODO: handle add user */ })
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topStart = ProfileDimens.ProfileCardCornerRadius,
                            topEnd = ProfileDimens.ProfileCardCornerRadius
                        )
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = ProfileDimens.ProfileContentPaddingVertical,
                            horizontal = ProfileDimens.ProfileContentPaddingHorizontal
                        )
                ) {
                    item {
                        UserInfoSection(conductor = conductor, onClick = { authViewModel.logout() })
                    }
                    item {
                        SectionSpacer()
                    }
                    item {
                        ProfileMenuItem(
                            icon = ImageVector.vectorResource(R.drawable.ic_personal_data),
                            text = stringResource(R.string.profile_personal_data),
                            onClick = { navController.navigate(Routes.PROFILE_PERSONAL_DATA) },
                            shape = ProfileMenuItemShape.Top
                        )
                        ProfileMenuItem(
                            icon = ImageVector.vectorResource(R.drawable.ic_settings),
                            text = stringResource(R.string.profile_settings),
                            onClick = { navController.navigate(Routes.PROFILE_SETTINGS) },
                            shape = ProfileMenuItemShape.Bottom
                        )
                    }
                    item {
                        SectionSpacer()
                    }
                    item {
                        ProfileMenuItem(
                            icon = ImageVector.vectorResource(R.drawable.ic_faqs),
                            text = stringResource(R.string.profile_faqs),
                            onClick = { navController.navigate(Routes.PROFILE_FAQS) },
                            shape = ProfileMenuItemShape.Top
                        )
                        ProfileMenuItem(
                            icon = ImageVector.vectorResource(R.drawable.ic_feedback),
                            text = stringResource(R.string.profile_feedback),
                            onClick = { navController.navigate(Routes.PROFILE_FEEDBACK) },
                            shape = ProfileMenuItemShape.Middle
                        )
                        ProfileMenuItem(
                            icon = ImageVector.vectorResource(R.drawable.ic_info),
                            text = stringResource(R.string.profile_about),
                            onClick = { navController.navigate(Routes.PROFILE_ABOUT) },
                            shape = ProfileMenuItemShape.Bottom
                        )
                    }
                }
            }
        }
    }

    if (uiState.showLogoutErrorDialog) {
        AlertDialog(
            onDismissRequest = { authViewModel.dismissLogoutErrorDialog() },
            title = {
                Text(text = stringResource(R.string.logout_error_title))
            },
            text = {
                Text(
                    text = uiState.logoutErrorMessage
                        ?: stringResource(R.string.logout_error_message)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { authViewModel.dismissLogoutErrorDialog() }
                ) {
                    Text(stringResource(R.string.logout_ok))
                }
            }
        )
    }

    if (uiState.showActiveShiftDialog) {
        AlertDialog(
            onDismissRequest = { authViewModel.dismissActiveShiftDialog() },
            title = {
                Text(text = stringResource(R.string.active_shift_title))
            },
            text = {
                Text(text = stringResource(R.string.active_shift_message))
            },
            confirmButton = {
                TextButton(
                    onClick = { authViewModel.dismissActiveShiftDialog() }
                ) {
                    Text(stringResource(R.string.logout_ok))
                }
            }
        )
    }
}