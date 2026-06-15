package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.ui.components.statistics.HeaderIconCell
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun rememberColumnWidths(): ColumnWidths {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    return remember(screenWidth, isLandscape) {
        if (isLandscape) {
            ColumnWidths(
                name = screenWidth * 0.25f,
                price = screenWidth * 0.12f,
                qty = screenWidth * 0.08f,
                revenue = screenWidth * 0.15f
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

internal data class ColumnWidths(val name: Dp, val price: Dp, val qty: Dp, val revenue: Dp)

internal val TableText = TextStyle(fontSize = 12.sp)

@Composable
internal fun rememberStatisticsTableScrollState(): ScrollState = rememberScrollState()

@Composable
internal fun TableHeader(
    scrollState: ScrollState,
    widths: ColumnWidths,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    TableLineContainer(modifier = modifier) {
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
            enableScroll = !isLandscape
        ) {
            HeaderIconCell(R.drawable.ic_rub, widths.price)
            HeaderIconCell(R.drawable.ic_add, widths.qty)
            HeaderIconCell(R.drawable.ic_replenish, widths.qty)
            HeaderIconCell(R.drawable.ic_cash, widths.qty)
            HeaderIconCell(R.drawable.ic_card, widths.qty)
            Box(
                modifier = Modifier.width(widths.revenue),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.cash_revenue_total),
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
internal fun TableRow(
    report: FastReportDomain,
    scrollState: ScrollState,
    widths: ColumnWidths,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    TableLineContainer(modifier = modifier) {
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

@Composable
private fun CenterScrollArea(
    scrollState: ScrollState,
    nameWidth: Dp,
    enableScroll: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
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
private fun TableLineContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) { content() }
}

@Composable
private fun NumericCell(
    text: String,
    color: Color,
    width: Dp,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(width),
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
