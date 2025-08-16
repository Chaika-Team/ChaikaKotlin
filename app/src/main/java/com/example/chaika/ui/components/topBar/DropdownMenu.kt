package com.example.chaika.ui.components.topBar

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.chaika.R

@Composable
fun ProfileDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        MenuItem.entries.forEach { item ->
            DropdownMenuItem(
                text = { Text(stringResource(item.titleResId)) },
                onClick = { onMenuItemClick(item) }
            )
        }
    }
}

enum class MenuItem(val titleResId: Int) {
    REFILL(R.string.refill),
}