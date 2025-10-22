package com.chaikasoft.app.ui.components.product

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.ui.viewModels.SaleViewModel
import kotlinx.coroutines.launch
import com.chaikasoft.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellResultBottomSheet(
    viewModel: SaleViewModel,
    onClick: () -> Unit
) {
    val state by viewModel.sellResultDialog.collectAsStateWithLifecycle()
    if (state == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                viewModel.dismissSellResultDialog()
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
                text = stringResource(R.string.sell_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(state!!.messageRes),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.dismissSellResultDialog()
                    }
                    onClick()
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
