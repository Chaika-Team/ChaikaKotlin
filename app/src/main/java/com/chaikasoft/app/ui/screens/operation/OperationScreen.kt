package com.chaikasoft.app.ui.screens.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.components.operation.OperationCard
import com.chaikasoft.app.ui.viewmodels.OperationViewModel

@Composable
fun OperationScreen(viewModel: OperationViewModel) {
    val operations = viewModel.operations.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("operationScreen")
    ) {
        items(operations.itemCount) { item ->
            operations[item]?.let {
                OperationCard(summary = it, viewModel = viewModel)
            }
        }

        operations.apply {
            when {
                loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                    item { LoadingItem(stringResource(R.string.operation_loading_operations)) }
                }

                loadState.refresh is LoadState.NotLoading && itemCount == 0 -> {
                    item { EmptyStateItem() }
                }

                loadState.append is LoadState.Loading -> {
                    item { LoadingItem(stringResource(R.string.operation_loading_more)) }
                }

                loadState.append is LoadState.Error -> {
                    item { ErrorItem(stringResource(R.string.operation_loading_error)) }
                }

                loadState.refresh is LoadState.Error -> {
                    item { ErrorItem(stringResource(R.string.operation_loading_error)) }
                }
            }
        }
    }
}

@Composable
private fun LoadingItem(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.width(8.dp))
        Text(message)
    }
}

@Composable
private fun EmptyStateItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.operation_nothing),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorItem(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.error
    )
}
