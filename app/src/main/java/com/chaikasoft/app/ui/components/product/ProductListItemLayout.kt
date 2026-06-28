package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.theme.ProductDimens

@Composable
internal fun ProductListItemLayout(
    product: Product,
    modifier: Modifier = Modifier,
    actionsBelowHeader: Boolean = false,
    actionRow: @Composable (Modifier) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                ProductImage(
                    imageUrl = product.image,
                    modifier = Modifier
                        .size(ProductDimens.CartProductItem.ImageSize)
                        .shadow(
                            elevation = 4.dp,
                            shape = MaterialTheme.shapes.large,
                            clip = false,
                            ambientColor = Color.Black.copy(alpha = 0.7f),
                            spotColor = Color.Black.copy(alpha = 0.7f)
                        )
                        .padding(end = ProductDimens.PaddingM)
                        .aspectRatio(1f),
                    contentDescription = product.name
                )
                if (actionsBelowHeader) {
                    ProductTitle(
                        product = product,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ProductDimens.CartProductItem.ImageSize)
                    ) {
                        ProductTitle(
                            product = product,
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                        actionRow(
                            Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                        )
                    }
                }
            }
            if (actionsBelowHeader) {
                actionRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProductTitle(product: Product, modifier: Modifier = Modifier) {
    Text(
        text = product.name,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = ProductDimens.CartProductItem.NameFontSize,
        lineHeight = 20.sp,
        maxLines = ProductDimens.CartProductItem.MaxNameLines,
        overflow = TextOverflow.Ellipsis
    )
}
