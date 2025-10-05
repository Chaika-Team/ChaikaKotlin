package com.chaikasoft.app.ui.components.operation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.ui.components.trip.dashedBorder
import com.chaikasoft.app.ui.theme.OperationDimens
import com.chaikasoft.app.ui.viewModels.OperationViewModel
import com.chaikasoft.app.util.formatPriceOnly
import com.chaikasoft.app.util.formatRuShort
import com.chaikasoft.app.util.toZoned
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

@Composable
fun OperationCard(summary: OperationSummaryDomain, viewModel: OperationViewModel) {
    val itemsFlow = remember(summary.id) { viewModel.getItems(summary.id) }
    val cart by itemsFlow.collectAsStateWithLifecycle(initialValue = null)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = OperationDimens.CardMaxHeight)
            .padding(OperationDimens.CardOuterPadding)
            .background(color = Color.White, shape = RoundedCornerShape(OperationDimens.CornerRadius))
            .dashedBorder(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(OperationDimens.CardInnerPadding)) {
            // --- Заголовок ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = summary.type.iconRes()),
                        contentDescription = null,
                        modifier = Modifier.size(OperationDimens.HeaderIconSize),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(OperationDimens.HeaderSpacer))
                    Text(
                        text = stringResource(summary.type.stringRes()),
                        fontWeight = FontWeight.Bold,
                        fontSize = OperationDimens.TitleFontSize,
                        color = Color.Black
                    )
                }
                Text(
                    text = summary.timeIso.toZoned().formatRuShort(),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(OperationDimens.BetweenSectionsSpacer))

            // --- Список товаров ---
            cart?.let { cartDomain ->
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = OperationDimens.ItemsMaxHeight) // оставляем место под футер
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        cartDomain.items.forEachIndexed { index, cartItem ->
                            ProductRow(
                                item = cartItem,
                                isSale = summary.type == OperationTypeDomain.SOLD_CASH ||
                                        summary.type == OperationTypeDomain.SOLD_CART
                            )
                            // Разделитель только между строками
                            if (index < cartDomain.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.LightGray,
                                    thickness = OperationDimens.DividerThickness
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(OperationDimens.BetweenSectionsSpacer))

            // --- Футер ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${summary.conductor.name} ${summary.conductor.familyName}",
                    fontSize = OperationDimens.FooterFontSize,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (summary.type == OperationTypeDomain.SOLD_CASH ||
                    summary.type == OperationTypeDomain.SOLD_CART
                ) {
                    Text(
                        text = formatPriceOnly(summary.totalPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = OperationDimens.FooterFontSize,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ProductRow(item: CartItemDomain, isSale: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OperationDimens.ProductRowVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Название
        Text(
            text = item.product.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(OperationDimens.QuantityGap))

        // Количество
        val qty = abs(item.quantity)
        val quantityFormatted = if (qty < 10) "0$qty" else "$qty"
        Text(
            text = "$quantityFormatted ${stringResource(R.string.op_quantity_suffix)}",
            color = MaterialTheme.colorScheme.secondary
        )

        if (isSale) {
            Spacer(modifier = Modifier.width(OperationDimens.QuantityGap))
            Text(
                text = formatPriceOnly(item.product.price),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// --- Helpers ---
@StringRes
fun OperationTypeDomain.stringRes(): Int = when (this) {
    OperationTypeDomain.ADD -> R.string.op_type_add
    OperationTypeDomain.SOLD_CASH -> R.string.op_type_sold_cash
    OperationTypeDomain.SOLD_CART -> R.string.op_type_sold_card
    OperationTypeDomain.REPLENISH -> R.string.op_type_replenish
}

@DrawableRes
fun OperationTypeDomain.iconRes(): Int = when (this) {
    OperationTypeDomain.ADD -> R.drawable.ic_bag
    OperationTypeDomain.REPLENISH -> R.drawable.ic_package_receive
    OperationTypeDomain.SOLD_CASH -> R.drawable.ic_cash_payment
    OperationTypeDomain.SOLD_CART -> R.drawable.ic_credit_card
}
