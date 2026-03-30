package com.chaikasoft.app.ui.screens.operation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.ui.components.operation.OperationCard
import com.chaikasoft.app.ui.viewModels.OperationViewModel

@Composable
fun OperationScreen(
    viewModel: OperationViewModel
) {
    val operations = viewModel.operations.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(operations.itemCount) { item ->
            operations[item]?.let {
                OperationCard(summary = it, viewModel = viewModel)
            }
        }

        operations.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingItem("Загрузка операций...") }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingItem("Загружаем ещё...") }
                }
                loadState.refresh is LoadState.Error -> {
                    item { ErrorItem("Ошибка загрузки") }
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
private fun ErrorItem(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.error
    )
}
