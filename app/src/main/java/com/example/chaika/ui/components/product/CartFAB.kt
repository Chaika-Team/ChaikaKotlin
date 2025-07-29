package com.example.chaika.ui.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import com.example.chaika.ui.components.trip.dashedBorder
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.stringResource
import com.example.chaika.ui.theme.ProductDimens

fun Modifier.drawOutlineFab(
    color: Color = Color.Gray,
    topCornerRadius: Dp = 28.dp,
    strokeWidth: Dp = 1.dp
) = this.then(
    Modifier.drawBehind {
        val w = size.width
        val h = size.height
        val r = topCornerRadius.toPx()
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, h)
            lineTo(0f, r)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(0f, 0f, 2 * r, 2 * r),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(w - r, 0f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(w - 2 * r, 0f, w, 2 * r),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(w, h)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
            )
        )
    }
)

@Composable
fun CartFAB(
    totalPrice: String,
    itemsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeSize = ProductDimens.CartFAB.BadgeSize
    val badgeOverlap = ProductDimens.CartFAB.BadgeOverlap
    Box(
        modifier = modifier
            .width(ProductDimens.CartFAB.Width)
            .height(ProductDimens.CartFAB.Height)
    ) {
        Box(
            modifier = Modifier
                .width(ProductDimens.CartFAB.InnerWidth)
                .height(ProductDimens.CartFAB.InnerHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = ProductDimens.CartFAB.TopCornerRadius,
                        topEnd = ProductDimens.CartFAB.TopCornerRadius,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onClick)
                .drawOutlineFab(topCornerRadius = ProductDimens.CartFAB.TopCornerRadius),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ProductDimens.CartFAB.ColumnPaddingHorizontal)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cart_buy),
                    contentDescription = stringResource(id = R.string.cart_fab_content_description),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ProductDimens.CartFAB.IconSize)
                )

                Spacer(modifier = Modifier.height(ProductDimens.CartFAB.SpacerHeight))

                Text(
                    text = String.format(stringResource(id = R.string.cart_fab_price_format), totalPrice),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = ProductDimens.CartFAB.PriceFontSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
        if (itemsCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = badgeOverlap, y = -badgeOverlap)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .dashedBorder(cornerRadius = ProductDimens.CartFAB.BadgeCornerRadius),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemsCount.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = ProductDimens.CartFAB.BadgeFontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(y = ProductDimens.CartFAB.BadgeTextOffsetY)
                )
            }
        }
    }
}

@Preview
@Composable
fun FabPrev() {
    CartFAB(totalPrice = "60 000", itemsCount = 1, onClick = {})
}