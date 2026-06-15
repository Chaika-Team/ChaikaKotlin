package com.chaikasoft.app.ui.components.product

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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (divTop, divBottom, imageRef, nameRef, actionRowRef) = createRefs()

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(divTop) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            ProductImage(
                imageUrl = product.image,
                modifier = Modifier
                    .constrainAs(imageRef) {
                        top.linkTo(divTop.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .size(ProductDimens.CartProductItem.ImageSize)
                    .padding(end = ProductDimens.PaddingM)
                    .aspectRatio(1f),
                contentDescription = product.name
            )

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = ProductDimens.CartProductItem.NameFontSize,
                maxLines = ProductDimens.CartProductItem.MaxNameLines,
                modifier = Modifier.constrainAs(nameRef) {
                    top.linkTo(imageRef.top)
                    start.linkTo(imageRef.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            actionRow(
                Modifier.constrainAs(actionRowRef) {
                    start.linkTo(nameRef.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(imageRef.bottom)
                    width = Dimension.fillToConstraints
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .constrainAs(divBottom) {
                        top.linkTo(imageRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}
