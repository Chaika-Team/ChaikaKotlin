package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
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
import com.chaikasoft.app.ui.viewModels.StatisticsViewModel
import java.text.NumberFormat
import java.util.Locale

private val NameColWidth = 120.dp
private val PriceColWidth = 50.dp
private val QtyColWidth = 32.dp
private val RevenueColWidth = 62.dp

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

    val sharedHScroll = rememberScrollState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Подтянем количество чеков при старте
    LaunchedEffect(Unit) { viewModel.refreshCashChecks() }

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
            val expanded =
                scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
            CashSummarySheet(
                cashRevenue = cashRevenue,
                checks = cashChecks,
                showChecks = expanded,
                showLabels = expanded,
                bottomPadding = if (expanded) 56.dp else 0.dp
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            stickyHeader { TableHeader(sharedHScroll) }
            items(reports) { report ->
                TableRow(report = report, scrollState = sharedHScroll)
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
            androidx.compose.animation.AnimatedVisibility(
                visible = showLabels,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cash),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Наличные", style = MaterialTheme.typography.titleMedium)
                }
            }

            Text(
                text = formatCurrency(cashRevenue),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }


        AnimatedVisibility(visible = showChecks) {
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
private fun TableHeader(scrollState: ScrollState) {
    TableLineContainer {
        Box(
            modifier = Modifier
                .width(NameColWidth)
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
        CenterScrollArea(scrollState) {
            HeaderIconCell(R.drawable.ic_rub,       PriceColWidth)
            HeaderIconCell(R.drawable.ic_add,       QtyColWidth)
            HeaderIconCell(R.drawable.ic_replenish, QtyColWidth)
            HeaderIconCell(R.drawable.ic_card,      QtyColWidth)
            HeaderIconCell(R.drawable.ic_cash,      QtyColWidth)
            Box(
                modifier = Modifier.width(RevenueColWidth),
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
    scrollState: ScrollState
) {
    TableLineContainer {
        Box(
            modifier = Modifier
                .width(NameColWidth)
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
        CenterScrollArea(scrollState) {
            NumericCell(formatNumber(report.productPrice),       Color.Gray,  PriceColWidth,  TableText)
            NumericCell(formatNumber(report.addedQuantity),      Color.Black, QtyColWidth,    TableText)
            NumericCell(formatNumber(report.replenishedQuantity),Color.Gray,  QtyColWidth,    TableText)
            NumericCell(formatNumber(report.soldCashQuantity),   Color.Black, QtyColWidth,    TableText)
            NumericCell(formatNumber(report.soldCartQuantity),   Color.Gray,  QtyColWidth,    TableText)
            NumericCell(formatNumber(report.revenue),            Color.Black, RevenueColWidth,TableText)
        }
    }
}

/** Центр с горизонтальным скроллом */
@Composable
private fun CenterScrollArea(
    scrollState: ScrollState,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = NameColWidth)
            .clipToBounds()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .wrapContentWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
private fun TableLineContainer(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(IntrinsicSize.Min)
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

private fun formatCurrency(value: Int): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return nf.format(value)
}
