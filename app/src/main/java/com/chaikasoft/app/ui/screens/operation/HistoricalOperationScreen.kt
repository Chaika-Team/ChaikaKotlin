package com.chaikasoft.app.ui.screens.operation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.HistoricalOperationDomain
import com.chaikasoft.app.ui.components.operation.OperationCard
import com.chaikasoft.app.ui.screens.util.HistoricalTripErrorContent
import com.chaikasoft.app.ui.screens.util.LoadingScreen
import com.chaikasoft.app.ui.state.HistoricalTripUiState
import com.chaikasoft.app.ui.viewmodels.HistoricalTripViewModel

@Composable
fun HistoricalOperationScreen(viewModel: HistoricalTripViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        HistoricalTripUiState.Loading -> LoadingScreen()
        is HistoricalTripUiState.Error -> HistoricalTripErrorContent(
            message = stringResource(currentState.messageRes)
        )
        is HistoricalTripUiState.Content -> HistoricalOperationContent(
            operations = currentState.snapshot.operations.withLocalizedFallbackNames()
        )
    }
}

@Composable
fun HistoricalOperationContent(
    operations: List<HistoricalOperationDomain>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("historicalOperationScreen")
    ) {
        items(
            items = operations,
            key = { it.summary.id }
        ) { operation ->
            OperationCard(summary = operation.summary, cart = operation.cart)
        }
    }
}

@Composable
private fun List<HistoricalOperationDomain>.withLocalizedFallbackNames():
    List<HistoricalOperationDomain> =
    buildList {
        this@withLocalizedFallbackNames.forEach { operation ->
            val conductor = operation.summary.conductor
            val conductorWithName = if (conductor.name.isBlank() &&
                conductor.familyName.isBlank()
            ) {
                conductor.copy(
                    name = stringResource(
                        R.string.historical_unknown_conductor_name,
                        conductor.employeeID
                    )
                )
            } else {
                conductor
            }
            val itemsWithNames = buildList {
                operation.cart.items.forEach { item ->
                    val product = item.product
                    add(
                        if (product.name.isBlank()) {
                            item.copy(
                                product = product.copy(
                                    name = stringResource(
                                        R.string.historical_unknown_product_name,
                                        product.id
                                    )
                                )
                            )
                        } else {
                            item
                        }
                    )
                }
            }
            add(
                operation.copy(
                    summary = operation.summary.copy(conductor = conductorWithName),
                    cart = operation.cart.copy(items = itemsWithNames)
                )
            )
        }
    }
