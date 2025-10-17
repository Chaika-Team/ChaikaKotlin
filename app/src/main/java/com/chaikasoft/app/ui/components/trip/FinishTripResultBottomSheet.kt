package com.chaikasoft.app.ui.components.trip

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.ui.viewModels.TripViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.chaikasoft.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishTripResultBottomSheet(
    viewModel: TripViewModel
) {
    val dialogState by viewModel.finishTripDialog.collectAsStateWithLifecycle()
    if (dialogState == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            // закрываем «жёстко», не оставляя полупозиций
            scope.launch {
                sheetState.hide()
                viewModel.dismissFinishTripDialog()
            }
        },
        sheetState = sheetState,
//        dragHandle = { ModalBottomSheetDefaults.DragHandle() },
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

            // одна понятная кнопка
            Button(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.dismissFinishTripDialog()
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
