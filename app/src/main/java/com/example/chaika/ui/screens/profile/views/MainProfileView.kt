package com.example.chaika.ui.screens.profile.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.example.chaika.R
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.ui.components.profile.ProfileMenuItem
import com.example.chaika.ui.components.profile.ProfileMenuItemShape
import com.example.chaika.ui.components.profile.SectionSpacer
import com.example.chaika.ui.components.profile.UserInfoSection
import com.example.chaika.ui.theme.ProfileDimens
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.viewModels.ProfileViewModel

@Composable
fun MainProfileView(
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    conductor: ConductorDomain?
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
                onClick = { viewModel.onPersonalDataClick() },
                shape = ProfileMenuItemShape.Top
            )
            ProfileMenuItem(
                icon = ImageVector.vectorResource(R.drawable.ic_settings),
                text = stringResource(R.string.profile_settings),
                onClick = { viewModel.onSettingsClick() },
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
                onClick = { viewModel.onFaqsClick() },
                shape = ProfileMenuItemShape.Top
            )
            ProfileMenuItem(
                icon = ImageVector.vectorResource(R.drawable.ic_feedback),
                text = stringResource(R.string.profile_feedback),
                onClick = { viewModel.onFeedbackClick() },
                shape = ProfileMenuItemShape.Middle
            )
            ProfileMenuItem(
                icon = ImageVector.vectorResource(R.drawable.ic_info),
                text = stringResource(R.string.profile_about),
                onClick = { viewModel.onAboutClick() },
                shape = ProfileMenuItemShape.Bottom
            )
        }
    }
}