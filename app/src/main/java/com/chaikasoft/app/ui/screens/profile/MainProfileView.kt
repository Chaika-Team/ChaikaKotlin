package com.chaikasoft.app.ui.screens.profile

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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.components.profile.ProfileMenuItem
import com.chaikasoft.app.ui.components.profile.ProfileMenuItemShape
import com.chaikasoft.app.ui.components.profile.SectionSpacer
import com.chaikasoft.app.ui.components.profile.UserHeaderSection
import com.chaikasoft.app.ui.components.profile.UserInfoSection
import com.chaikasoft.app.ui.components.profile.ConfirmBottomSheet
import com.chaikasoft.app.ui.theme.ProfileBackground
import com.chaikasoft.app.ui.theme.ProfileDimens
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import androidx.navigation.NavHostController
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.ConductorViewModel
import com.chaikasoft.app.ui.components.trip.FinishTripResultBottomSheet
import com.chaikasoft.app.ui.viewModels.TripViewModel

@Composable
fun MainProfileView(
    conductorViewModel: ConductorViewModel,
    authViewModel: AuthViewModel,
    tripViewModel: TripViewModel,
    navController: NavHostController
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val conductor by conductorViewModel.conductor.collectAsStateWithLifecycle()

    val selectedTripRecord by tripViewModel.selectedTripRecord.collectAsStateWithLifecycle(initialValue = null)

    var showLogoutConfirmSheet by remember { mutableStateOf(false) }
    var showFinishTripConfirmSheet by remember { mutableStateOf(false) }
    var pendingLogoutAfterTripFinish by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            UserHeaderSection(conductor = conductor, onClick = { /* nothing for now */ })
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
                        UserInfoSection(conductor = conductor) {
                            if (selectedTripRecord != null) {
                                showFinishTripConfirmSheet = true
                            } else {
                                showLogoutConfirmSheet = true
                            }
                        }
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
            title = { Text(text = stringResource(R.string.logout_error_title)) },
            text = {
                Text(text = uiState.logoutErrorMessage ?: stringResource(R.string.logout_error_message))
            },
            confirmButton = {
                TextButton(onClick = { authViewModel.dismissLogoutErrorDialog() }) {
                    Text(stringResource(R.string.logout_ok))
                }
            }
        )
    }

    ConfirmBottomSheet(
        visible = showLogoutConfirmSheet,
        title = stringResource(R.string.logout_confirm_title),
        message = stringResource(R.string.logout_confirm_message),
        confirmText = stringResource(R.string.logout_confirm),
        cancelText = stringResource(R.string.logout_cancel),
        onConfirm = {
            showLogoutConfirmSheet = false
            authViewModel.logout()
        },
        onDismiss = { showLogoutConfirmSheet = false }
    )

    ConfirmBottomSheet(
        visible = showFinishTripConfirmSheet,
        title = stringResource(R.string.active_trip_confirm_title),
        message = stringResource(R.string.active_trip_confirm_message),
        confirmText = stringResource(R.string.finish_trip),
        cancelText = stringResource(R.string.logout_cancel),
        onConfirm = {
            tripViewModel.finishCurrentTrip()
            showFinishTripConfirmSheet = false
            pendingLogoutAfterTripFinish = true
        },
        onDismiss = { showFinishTripConfirmSheet = false }
    )

    FinishTripResultBottomSheet(
        tripViewModel = tripViewModel,
        pendingLogout = pendingLogoutAfterTripFinish,
        onDismissWithLogout = {
            if (pendingLogoutAfterTripFinish) {
                pendingLogoutAfterTripFinish = false
                showLogoutConfirmSheet = true
            }
        }
    )
}
