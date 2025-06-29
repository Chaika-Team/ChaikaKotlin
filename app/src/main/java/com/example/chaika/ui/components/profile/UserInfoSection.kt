package com.example.chaika.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.R
import com.example.chaika.ui.components.trip.dashedBorder
import androidx.compose.ui.res.stringResource
import com.example.chaika.ui.theme.ProfileDimens

@Composable
fun UserInfoSection(conductor: ConductorDomain?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = listOfNotNull(conductor?.familyName, conductor?.name).joinToString(" ").ifBlank { "Имя Фамилия" },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "ID: ${conductor?.employeeID ?: "-"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            )
        }

        Box(
            modifier = Modifier
                .size(ProfileDimens.LogoutCornerRadius)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onClick)
                .dashedBorder(cornerRadius = ProfileDimens.LogoutCornerRadius),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_logout),
                contentDescription = stringResource(R.string.profile_navigate),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(ProfileDimens.LogoutButtonSize)
            )
        }
    }
}