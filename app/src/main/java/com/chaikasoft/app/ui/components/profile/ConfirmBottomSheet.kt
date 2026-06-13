package com.chaikasoft.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = stringResource(id = android.R.string.ok),
    cancelText: String = stringResource(id = android.R.string.cancel),
    confirmButtonTag: String? = null,
    cancelButtonTag: String? = null,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun dismiss(afterDismiss: () -> Unit = {}) {
        scope.launch {
            sheetState.hide()
            onDismiss()
            afterDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { dismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    dismiss(afterDismiss = onConfirm)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (confirmButtonTag != null) {
                            Modifier.testTag(confirmButtonTag)
                        } else {
                            Modifier
                        }
                    ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(text = confirmText)
            }
            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { dismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (cancelButtonTag != null) {
                            Modifier.testTag(cancelButtonTag)
                        } else {
                            Modifier
                        }
                    )
            ) {
                Text(text = cancelText)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
