package com.example.chaika.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.ProfileDimens

@Composable
fun SectionSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(ProfileDimens.SectionSpacerHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    )
}