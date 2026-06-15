package com.chaikasoft.app.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.util.formatPriceOnly

@Composable
internal fun CashSummarySheet(
    cashRevenue: Int,
    cashlessChecks: Int,
    showChecks: Boolean,
    showLabels: Boolean,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val cashRevenueText = remember(cashRevenue) { formatPriceOnly(cashRevenue) }

    Column(
        modifier = modifier
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
                Text(
                    stringResource(R.string.cashless_checks),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(cashlessChecks.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
