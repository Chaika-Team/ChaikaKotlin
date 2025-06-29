package com.example.chaika.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaika.R
import com.example.chaika.ui.theme.ProfileDimens
import com.example.chaika.ui.theme.ProfileMenuItemBackground

enum class ProfileMenuItemShape {
    Top, Middle, Bottom, Single
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    shape: ProfileMenuItemShape = ProfileMenuItemShape.Middle
) {
    val cornerShape = when (shape) {
        ProfileMenuItemShape.Top -> RoundedCornerShape(
            topStart = ProfileDimens.MenuItemCornerRadiusTop, 
            topEnd = ProfileDimens.MenuItemCornerRadiusTop
        )
        ProfileMenuItemShape.Middle -> RoundedCornerShape(0.dp)
        ProfileMenuItemShape.Bottom -> RoundedCornerShape(
            bottomStart = ProfileDimens.MenuItemCornerRadiusBottom, 
            bottomEnd = ProfileDimens.MenuItemCornerRadiusBottom
        )
        ProfileMenuItemShape.Single -> RoundedCornerShape(ProfileDimens.MenuItemCornerRadiusSingle)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cornerShape)
            .background(ProfileMenuItemBackground)
            .clickable(onClick = onClick)
            .padding(
                horizontal = ProfileDimens.MenuItemPaddingHorizontal, 
                vertical = ProfileDimens.MenuItemPaddingVertical
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ProfileDimens.MenuItemIconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.profile_menu_item_icon_description),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(ProfileDimens.MenuItemIconInnerSize)
            )
        }

        Spacer(modifier = Modifier.width(ProfileDimens.MenuItemSpacerWidth))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
            contentDescription = stringResource(R.string.navigate),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}