package com.chaikasoft.app.ui.components.trip

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.viewModels.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun finishTripResultBottomSheet(
    tripViewModel: TripViewModel,
    pendingLogout: Boolean = true,
    onDismissWithLogout: () -> Unit
) {
    val dialogState by tripViewModel.finishTripDialog.collectAsStateWithLifecycle()

    val shouldShowSheet = dialogState != null && pendingLogout

    if (!shouldShowSheet) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                tripViewModel.dismissFinishTripDialog()

                if (pendingLogout) {
                    onDismissWithLogout()
                }
            }
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.trip_finish_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(dialogState!!.messageRes),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        tripViewModel.dismissFinishTripDialog()

                        if (pendingLogout) {
                            onDismissWithLogout()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(text = stringResource(android.R.string.ok))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
