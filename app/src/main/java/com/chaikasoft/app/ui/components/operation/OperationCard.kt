package com.chaikasoft.app.ui.components.operation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.ui.components.trip.dashedBorder
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.OperationDimens
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.util.formatPriceOnly
import com.chaikasoft.app.util.formatRuShort
import com.chaikasoft.app.util.toZoned
import kotlin.math.abs

@Composable
fun OperationCard(
    summary: OperationSummaryDomain,
    cart: CartDomain?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(OperationDimens.CardOuterPadding)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(OperationDimens.CornerRadius)
            )
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
                val itemsScrollState = rememberScrollState()
                val itemsNestedScrollConnection = rememberItemsNestedScrollConnection(
                    scrollState = itemsScrollState
                )

                Box(
                    modifier = Modifier
                        .heightIn(max = OperationDimens.ItemsMaxHeight) // оставляем место под футер
                        .nestedScroll(itemsNestedScrollConnection)
                        .verticalScroll(itemsScrollState)
                ) {
                    Column {
                        cartDomain.items.forEachIndexed { index, cartItem ->
                            ProductRow(
                                item = cartItem,
                                isSale = summary.type == OperationTypeDomain.SOLD_CASH ||
                                    summary.type == OperationTypeDomain.SOLD_CARD
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
                val conductorName = listOf(
                    summary.conductor.name,
                    summary.conductor.familyName
                ).filter { it.isNotBlank() }.joinToString(" ")
                Text(
                    text = conductorName,
                    fontSize = OperationDimens.FooterFontSize,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (summary.type == OperationTypeDomain.SOLD_CASH ||
                    summary.type == OperationTypeDomain.SOLD_CARD
                ) {
                    Spacer(modifier = Modifier.width(OperationDimens.QuantityGap))
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
private fun rememberItemsNestedScrollConnection(scrollState: ScrollState): NestedScrollConnection =
    remember(scrollState) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset = if (source.consumesItemsScrollOverflow() && scrollState.maxValue > 0) {
                Offset(x = 0f, y = available.y)
            } else {
                Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity =
                if (scrollState.shouldConsumeOutgoingFling(available.y)) {
                    Velocity(x = 0f, y = available.y)
                } else {
                    Velocity.Zero
                }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                if (scrollState.maxValue > 0) {
                    Velocity(x = 0f, y = available.y)
                } else {
                    Velocity.Zero
                }
        }
    }

private fun NestedScrollSource.consumesItemsScrollOverflow(): Boolean =
    this == NestedScrollSource.UserInput || this == NestedScrollSource.SideEffect

private fun ScrollState.shouldConsumeOutgoingFling(velocityY: Float): Boolean {
    if (maxValue <= 0) return false

    val flingPastTop = value <= 0 && velocityY > 0f
    val flingPastBottom = value >= maxValue && velocityY < 0f

    return flingPastTop || flingPastBottom
}

@Composable
private fun ProductRow(item: CartItemDomain, isSale: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OperationDimens.ProductRowVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название
        Text(
            text = item.product.name,
            maxLines = 2,
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
    OperationTypeDomain.SOLD_CARD -> R.string.op_type_sold_card
    OperationTypeDomain.REPLENISH -> R.string.op_type_replenish
}

@DrawableRes
fun OperationTypeDomain.iconRes(): Int = when (this) {
    OperationTypeDomain.ADD -> R.drawable.ic_bag
    OperationTypeDomain.REPLENISH -> R.drawable.ic_package_receive
    OperationTypeDomain.SOLD_CASH -> R.drawable.ic_cash_payment
    OperationTypeDomain.SOLD_CARD -> R.drawable.ic_credit_card
}

@PhoneScalablePreviews
@Composable
private fun OperationCardPreview() {
    ChaikaTheme {
        OperationCard(
            summary = previewOperationSummary(),
            cart = previewOperationCart()
        )
    }
}

private fun previewOperationSummary(): OperationSummaryDomain = OperationSummaryDomain(
    id = 1,
    type = OperationTypeDomain.SOLD_CASH,
    timeIso = "2026-06-25T12:30:00+03:00",
    conductor = ConductorDomain(
        id = 1,
        name = "Александр",
        familyName = "Константинопольский",
        givenName = "Владимирович",
        employeeID = "EMP001",
        image = ""
    ),
    productLineQuantity = 2,
    totalPrice = 78_000
)

private fun previewOperationCart(): CartDomain = CartDomain(
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
