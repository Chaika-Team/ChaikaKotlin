package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.ui.screens.util.HistoricalTripErrorContent
import com.chaikasoft.app.ui.screens.util.LoadingScreen
import com.chaikasoft.app.ui.state.HistoricalTripUiState
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.HistoricalTripViewModel

@Composable
fun HistoricalStatisticsScreen(viewModel: HistoricalTripViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        HistoricalTripUiState.Loading -> LoadingScreen()
        is HistoricalTripUiState.Error -> HistoricalTripErrorContent(
            message = stringResource(currentState.messageRes)
        )
        is HistoricalTripUiState.Content -> StatisticsContent(
            reports = currentState.snapshot.statistics.withLocalizedFallbackNames(),
            cashRevenue = currentState.snapshot.cashRevenue,
            cashlessChecks = currentState.snapshot.cashlessChecksCount
        )
    }
}

@PhoneScalablePreviews
@Composable
private fun HistoricalStatisticsContentPreview() {
    ChaikaTheme {
        StatisticsContent(
            reports = previewHistoricalReports(),
            cashRevenue = 60_000,
            cashlessChecks = 2
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun HistoricalStatisticsContentWidePreview() {
    ChaikaTheme {
        StatisticsContent(
            reports = previewHistoricalReports(),
            cashRevenue = 60_000,
            cashlessChecks = 2
        )
    }
}

private fun previewHistoricalReports(): List<FastReportDomain> = listOf(
    FastReportDomain(
        productName = "Чай черный крупнолистовой с очень длинным названием",
        productPrice = 20_000,
        addedQuantity = 12,
        replenishedQuantity = 4,
        soldCashQuantity = 3,
        soldCardQuantity = 2,
        revenue = 60_000,
        productId = 1
    ),
    FastReportDomain(
        productName = "Вода негазированная",
        productPrice = 19_000,
        addedQuantity = 8,
        replenishedQuantity = 2,
        soldCashQuantity = 2,
        soldCardQuantity = 1,
        revenue = 38_000,
        productId = 2
    )
)

@Composable
private fun List<FastReportDomain>.withLocalizedFallbackNames(): List<FastReportDomain> =
    buildList {
        this@withLocalizedFallbackNames.forEach { report ->
            val fallbackProductId = report.productId
            val reportWithName = if (report.productName.isBlank() && fallbackProductId != null) {
                report.copy(
                    productName = stringResource(
                        R.string.historical_unknown_product_name,
                        fallbackProductId
                    )
                )
            } else {
                report
            }
            add(reportWithName)
        }
    }
