package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.ui.components.statistics.HeaderIconCell
import com.chaikasoft.app.ui.viewmodels.StatisticsViewModel
import com.chaikasoft.app.util.formatPriceOnly
import java.text.NumberFormat
import java.util.Locale

@Composable
private fun rememberColumnWidths(): ColumnWidths {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return remember(screenWidth) {
        if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            ColumnWidths(
                name = screenWidth * 0.25f, // 25% для названия
                price = screenWidth * 0.12f, // 12% для цены
                qty = screenWidth * 0.08f, // 8% для каждой qty колонки
                revenue = screenWidth * 0.15f // 15% для итога
            )
        } else {
            ColumnWidths(
                name = 120.dp,
                price = 50.dp,
                qty = 32.dp,
                revenue = 62.dp
            )
        }
    }
}

private data class ColumnWidths(val name: Dp, val price: Dp, val qty: Dp, val revenue: Dp)

private val TableText = TextStyle(fontSize = 12.sp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val cashRevenue by viewModel.cashRevenue.collectAsStateWithLifecycle()
    val cashChecks by viewModel.cashChecksCount.collectAsStateWithLifecycle()

    val columnWidths = rememberColumnWidths()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val sharedHScroll = rememberScrollState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(Unit) { viewModel.refreshCartChecks() }

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
                checks = cashChecks,
                showChecks = show,
                showLabels = show,
                bottomPadding = if (show) 56.dp else 0.dp
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
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
            items(
                items = reports,
                key = { it.productName }
            ) { report ->
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

@Composable
private fun CashSummarySheet(
    cashRevenue: Int,
    checks: Int,
    showChecks: Boolean,
    showLabels: Boolean,
    bottomPadding: Dp
) {
    val cashRevenueText = remember(cashRevenue) { formatPriceOnly(cashRevenue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
        ) {
            if (showLabels) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cash),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Наличные", style = MaterialTheme.typography.titleMedium)
                }
            }

            Text(
                text = cashRevenueText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (showChecks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Чеков (нал.)", style = MaterialTheme.typography.bodyMedium)
                Text(checks.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun TableHeader(scrollState: ScrollState, widths: ColumnWidths, isLandscape: Boolean) {
    TableLineContainer {
        Box(
            modifier = Modifier
                .width(widths.name)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "Товары",
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TableText
            )
        }
        CenterScrollArea(
            scrollState = scrollState,
            nameWidth = widths.name,
            enableScroll = !isLandscape // В landscape отключаем скролл
        ) {
            HeaderIconCell(R.drawable.ic_rub, widths.price)
            HeaderIconCell(R.drawable.ic_add, widths.qty)
            HeaderIconCell(R.drawable.ic_replenish, widths.qty)
            HeaderIconCell(R.drawable.ic_card, widths.qty)
            HeaderIconCell(R.drawable.ic_cash, widths.qty)
            Box(
                modifier = Modifier.width(widths.revenue),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Итог",
                    color = Color.Black,
                    maxLines = 1,
                    style = TableText,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun TableRow(
    report: FastReportDomain,
    scrollState: ScrollState,
    widths: ColumnWidths,
    isLandscape: Boolean
) {
    TableLineContainer {
        Box(
            modifier = Modifier
                .width(widths.name)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = report.productName,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TableText
            )
        }
        CenterScrollArea(
            scrollState = scrollState,
            nameWidth = widths.name,
            enableScroll = !isLandscape
        ) {
            NumericCell(
                formatNumber(report.productPrice.toDouble() / 100),
                Color.Gray,
                widths.price,
                TableText
            )
            NumericCell(report.addedQuantity.toString(), Color.Black, widths.qty, TableText)
            NumericCell(report.replenishedQuantity.toString(), Color.Gray, widths.qty, TableText)
            NumericCell(report.soldCashQuantity.toString(), Color.Black, widths.qty, TableText)
            NumericCell(report.soldCartQuantity.toString(), Color.Gray, widths.qty, TableText)
            NumericCell(
                formatNumber(report.revenue.toDouble() / 100),
                Color.Black,
                widths.revenue,
                TableText
            )
        }
    }
}

/** Центр с горизонтальным скроллом */
@Composable
private fun CenterScrollArea(
    scrollState: ScrollState,
    nameWidth: Dp,
    enableScroll: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = nameWidth)
            .clipToBounds()
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (enableScroll) {
                        Modifier.horizontalScroll(scrollState).wrapContentWidth()
                    } else {
                        Modifier.fillMaxWidth()
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (enableScroll) {
                Arrangement.Start
            } else {
                Arrangement.SpaceBetween
            },
            content = content
        )
    }
}

@Composable
private fun TableLineContainer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) { content() }
}

@Composable
private fun NumericCell(text: String, color: Color, width: Dp, style: TextStyle) {
    Box(
        modifier = Modifier.width(width),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = style
        )
    }
}

private fun formatNumber(number: Number): String {
    val nf = NumberFormat.getNumberInstance(Locale.getDefault())
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = if (number is Int) 2 else 0
    return nf.format(number)
}
