package com.example.chaika.ui.components.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.theme.ChaikaTheme
import com.example.chaika.ui.theme.ProductDimens
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.util.formatPrice
import androidx.compose.ui.res.stringResource
import com.example.chaika.R


@Composable
fun CartProductItem(
    product: Product,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit,
    onRemoveFromPackage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (divTop, divBottom, imageRef, nameRef, priceRowRef) = createRefs()

            // Верхний разделитель
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(divTop) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Изображение продукта
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

            // Название продукта
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

            // Строка с ценой и действиями
            CartProductActionsRow(
                product = product,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease,
                onAddToCart = onAddToCart,
                onRemove = onRemove,
                onRemoveFromPackage = onRemoveFromPackage,
                modifier = Modifier.constrainAs(priceRowRef) {
                    start.linkTo(nameRef.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(imageRef.bottom)
                    width = Dimension.fillToConstraints
                }
            )

            // Нижний разделитель
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

@Composable
private fun CartProductActionsRow(
    product: Product,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit,
    onRemoveFromPackage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ProductDimens.CartProductItem.QuantitySelectorHeight)
    ) {
        // Цена слева
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = formatPrice(product.price, product.quantity),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = ProductDimens.CartProductItem.PriceFontSize
            )
            if (product.isInPackage) {
                Text(
                    text = stringResource(id = R.string.cart_product_available, product.packageQuantity),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = ProductDimens.TextSizeSmall
                )
            }
        }

        if (product.isInCart) {
            QuantitySelector(
                quantity = product.quantity,
                onIncrease = onQuantityIncrease,
                onDecrease = onQuantityDecrease,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = ProductDimens.CartProductItem.QuantitySelectorWidth, height = ProductDimens.CartProductItem.QuantitySelectorHeight),
                colorBack = MaterialTheme.colorScheme.surfaceVariant,
                colorText = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = ProductDimens.CartProductItem.QuantitySelectorWidth, height = ProductDimens.CartProductItem.QuantitySelectorHeight),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primary),
                onClick = onAddToCart,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.cart_product_add_to_cart),
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.75F)
                )
            }
        }

        if (product.isInCart) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-ProductDimens.CartProductItem.QuantitySelectorWidth) - ProductDimens.CartProductItem.RemoveButtonPadding) // 8dp левее селектора
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.cart_product_remove_from_cart),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        } else if (product.isInPackage && onRemoveFromPackage != null) {
            IconButton(
                onClick = onRemoveFromPackage,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-ProductDimens.CartProductItem.QuantitySelectorWidth) - ProductDimens.CartProductItem.RemoveButtonPadding)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.cart_product_remove_from_package),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartProductItemPreview() {
    ChaikaTheme {
        CartProductItem(
            product = Product(
                id = 1,
                name = "Яблочный сок",
                description = "Свежий яблочный сок",
                image = "",
                price = 120.0,
                isInCart = true,
                quantity = 2
            ),
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onAddToCart = {},
            onRemove = {},
            onRemoveFromPackage = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartProductItemNotInCartPreview() {
    ChaikaTheme {
        CartProductItem(
            product = Product(
                id = 2,
                name = "Черный чай",
                description = "Ароматный черный чай",
                image = "",
                price = 85.0,
                isInCart = false,
                quantity = 1
            ),
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onAddToCart = {},
            onRemove = {},
            onRemoveFromPackage = {}
        )
    }
} 