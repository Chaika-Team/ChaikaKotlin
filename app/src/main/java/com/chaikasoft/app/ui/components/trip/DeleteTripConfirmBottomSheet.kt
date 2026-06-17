package com.chaikasoft.app.ui.components.trip

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.viewmodels.TripViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteTripConfirmBottomSheet(tripViewModel: TripViewModel) {
    val dialogState by tripViewModel.deleteTripDialog.collectAsStateWithLifecycle()
    val dialog = dialogState ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun dismiss() {
        if (dialog.isDeleting) return
        scope.launch {
            sheetState.hide()
            tripViewModel.dismissDeleteTripDialog()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { dismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        DeleteTripConfirmContent(
            hasPackageItems = dialog.hasPackageItems,
            preservePackage = dialog.preservePackage,
            isDeleting = dialog.isDeleting,
            errorMessageRes = dialog.errorMessageRes,
            onPreservePackageChanged = tripViewModel::onPreservePackageChanged,
            onConfirm = tripViewModel::confirmDeleteCurrentTrip
        )
    }
}

@Composable
private fun DeleteTripConfirmContent(
    hasPackageItems: Boolean?,
    preservePackage: Boolean,
    isDeleting: Boolean,
    @StringRes errorMessageRes: Int?,
    onPreservePackageChanged: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deleteTripBottomSheet")
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.trip_delete_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.trip_delete_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PreservePackageOption(
            visible = hasPackageItems == true,
            preservePackage = preservePackage,
            enabled = !isDeleting,
            onCheckedChange = onPreservePackageChanged
        )
        DeleteTripError(errorMessageRes)
        Spacer(Modifier.height(20.dp))
        DeleteTripConfirmButton(
            isDeleting = isDeleting,
            enabled = hasPackageItems != null && !isDeleting,
            onClick = onConfirm
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PreservePackageOption(
    visible: Boolean,
    preservePackage: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    if (!visible) return

    Spacer(Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = preservePackage,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.testTag("deleteTripPreservePackageCheckbox")
        )
        Text(text = stringResource(R.string.trip_delete_preserve_package))
    }
    Text(
        text = stringResource(
            if (preservePackage) {
                R.string.trip_delete_package_restored
            } else {
                R.string.trip_delete_package_cleared
            }
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DeleteTripError(@StringRes errorMessageRes: Int?) {
    if (errorMessageRes == null) return

    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(errorMessageRes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.testTag("deleteTripErrorMessage")
    )
}

@Composable
private fun DeleteTripConfirmButton(isDeleting: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deleteTripConfirmButton"),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        if (isDeleting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = stringResource(R.string.trip_delete_confirm))
        }
    }
}
