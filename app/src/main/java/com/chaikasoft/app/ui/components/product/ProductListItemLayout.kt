package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.theme.ProductDimens

@Composable
internal fun ProductListItemLayout(
    product: Product,
    modifier: Modifier = Modifier,
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
                    .padding(vertical = 8.dp)
            ) {
                ProductImage(
                    imageUrl = product.image,
                    modifier = Modifier
                        .size(ProductDimens.CartProductItem.ImageSize)
                        .padding(end = ProductDimens.PaddingM)
                        .aspectRatio(1f),
                    contentDescription = product.name
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = ProductDimens.CartProductItem.NameFontSize,
                        maxLines = ProductDimens.CartProductItem.MaxNameLines
                    )
                    actionRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
