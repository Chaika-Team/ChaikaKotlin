package com.chaikasoft.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = stringResource(id = android.R.string.ok),
    cancelText: String = stringResource(id = android.R.string.cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonTag: String? = null,
    cancelButtonTag: String? = null
) {
    if (!visible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = title, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = message, modifier = Modifier.padding(bottom = 16.dp))
            // actions
            // confirm button primary
            Button(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier
                    .then(
                        if (confirmButtonTag != null) {
                            Modifier.testTag(confirmButtonTag)
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxWidth()
            ) {
                Text(text = confirmText)
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .then(
                        if (cancelButtonTag != null) {
                            Modifier.testTag(cancelButtonTag)
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(text = cancelText)
            }
        }
    }
}
