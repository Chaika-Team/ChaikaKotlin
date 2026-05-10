package com.chaikasoft.app.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.ui.theme.ProfileBackground
import com.chaikasoft.app.ui.theme.ProfileDimens

@Composable
fun UserHeaderSection(conductor: ConductorDomain?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileBackground)
            .padding(ProfileDimens.HeaderPadding),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(ProfileDimens.AvatarSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val avatarFallback = painterResource(id = R.drawable.placeholder_chaika)
            AsyncImage(
                model = conductor?.image?.takeIf { it.isNotBlank() },
                placeholder = avatarFallback,
                error = avatarFallback,
                fallback = avatarFallback,
                contentDescription = stringResource(R.string.profile_user_avatar),
                modifier = Modifier
                    .size(ProfileDimens.AvatarSize)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(ProfileDimens.HeaderSpacerWidth))

        VerticalDivider(
            modifier = Modifier
                .height(ProfileDimens.DividerHeight)
                .width(ProfileDimens.DividerWidth),
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.width(ProfileDimens.HeaderSpacerWidth))

        Box(
            modifier = Modifier
                .size(ProfileDimens.AddButtonSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.profile_add_user),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(ProfileDimens.AddButtonIconSize)
            )
        }
    }
}
