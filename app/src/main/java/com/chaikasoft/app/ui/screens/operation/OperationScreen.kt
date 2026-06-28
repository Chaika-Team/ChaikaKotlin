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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.ui.components.operation.OperationCard
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
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
            operations[item]?.let { summary ->
                val itemsFlow = remember(summary.id) { viewModel.getItems(summary.id) }
                val cart by itemsFlow.collectAsStateWithLifecycle(initialValue = null)

                OperationCard(summary = summary, cart = cart)
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

@PhoneScalablePreviews
@Composable
private fun OperationScreenContentPreview() {
    ChaikaTheme {
        OperationPreviewContent()
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun OperationScreenContentWidePreview() {
    ChaikaTheme {
        OperationPreviewContent()
    }
}

@Composable
private fun OperationPreviewContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("operationScreen")
    ) {
        items(previewOperations().size) { index ->
            val operation = previewOperations()[index]
            OperationCard(summary = operation.summary, cart = operation.cart)
        }
    }
}

private data class PreviewOperation(val summary: OperationSummaryDomain, val cart: CartDomain)

private fun previewOperations(): List<PreviewOperation> {
    val conductor = ConductorDomain(
        id = 1,
        name = "Александр",
        familyName = "Константинопольский",
        givenName = "Владимирович",
        employeeID = "EMP001",
        image = ""
    )
    val cart = CartDomain(
        items = listOf(
            CartItemDomain(
                product = ProductInfoDomain(
                    id = 1,
                    name = "Чай черный крупнолистовой с очень длинным названием",
                    description = "Горячий напиток",
                    image = "",
                    price = 20_000
                ),
                quantity = 2
            ),
            CartItemDomain(
                product = ProductInfoDomain(
                    id = 2,
                    name = "Вода негазированная",
                    description = "500 мл",
                    image = "",
                    price = 19_000
                ),
                quantity = 2
            )
        )
    )
    return listOf(
        PreviewOperation(
            summary = OperationSummaryDomain(
                id = 1,
                type = OperationTypeDomain.SOLD_CASH,
                timeIso = "2026-06-25T12:30:00+03:00",
                conductor = conductor,
                productLineQuantity = 2,
                totalPrice = 78_000
            ),
            cart = cart
        ),
        PreviewOperation(
            summary = OperationSummaryDomain(
                id = 2,
                type = OperationTypeDomain.REPLENISH,
                timeIso = "2026-06-25T14:10:00+03:00",
                conductor = conductor,
                productLineQuantity = 2,
                totalPrice = 0
            ),
            cart = cart
        )
    )
}
