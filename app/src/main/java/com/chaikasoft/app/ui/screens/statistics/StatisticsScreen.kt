package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val cashRevenue by viewModel.cashRevenue.collectAsStateWithLifecycle()
    val cashlessChecks by viewModel.cashlessChecksCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refreshCashlessChecks() }

    StatisticsContent(
        reports = reports,
        cashRevenue = cashRevenue,
        cashlessChecks = cashlessChecks,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun StatisticsContent(
    reports: List<FastReportDomain>,
    cashRevenue: Int,
    cashlessChecks: Int,
    modifier: Modifier = Modifier
) {
    val columnWidths = rememberColumnWidths()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val sharedHScroll = rememberStatisticsTableScrollState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 72.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.padding(top = 4.dp, bottom = 0.dp)
            )
        },
        sheetContent = {
            val sheetState = scaffoldState.bottomSheetState
            val show =
                sheetState.targetValue == SheetValue.Expanded ||
                    sheetState.currentValue == SheetValue.Expanded

            CashSummarySheet(
                cashRevenue = cashRevenue,
                cashlessChecks = cashlessChecks,
                showChecks = show,
                showLabels = show,
                bottomPadding = if (show) 56.dp else 0.dp
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .testTag("statisticsScreen")
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 0.dp
                ) {
                    TableHeader(
                        scrollState = sharedHScroll,
                        widths = columnWidths,
                        isLandscape = isLandscape
                    )
                }
            }
            itemsIndexed(
                items = reports,
                key = { index, report -> "${report.productName}_${report.productPrice}_$index" }
            ) { _, report ->
                TableRow(
                    report = report,
                    scrollState = sharedHScroll,
                    widths = columnWidths,
                    isLandscape = isLandscape
                )
            }
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun StatisticsContentPreview() {
    ChaikaTheme {
        StatisticsContent(
            reports = previewReports(),
            cashRevenue = 156_000,
            cashlessChecks = 4
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun StatisticsContentWidePreview() {
    ChaikaTheme {
        StatisticsContent(
            reports = previewReports(),
            cashRevenue = 156_000,
            cashlessChecks = 4
        )
    }
}

private fun previewReports(): List<FastReportDomain> = listOf(
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
