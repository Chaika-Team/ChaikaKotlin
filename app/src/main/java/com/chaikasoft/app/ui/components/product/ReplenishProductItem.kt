package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.ProductDimens

@Composable
fun ReplenishProductItem(
    product: Product,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit,
    packageQuantity: Int,
    modifier: Modifier = Modifier
) {
    ProductListItemLayout(
        product = product,
        modifier = modifier
    ) { actionRowModifier ->
        StockQuantityRow(
            product = product,
            quantityToShow = packageQuantity + product.quantity,
            onAddToCart = onAddToCart,
            onQuantityIncrease = onQuantityIncrease,
            onQuantityDecrease = onQuantityDecrease,
            onRemove = onRemove,
            modifier = actionRowModifier
        )
    }
}

@Composable
private fun StockQuantityRow(
    product: Product,
    quantityToShow: Int,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ProductDimens.CartProductItem.QuantitySelectorHeight)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            val qtyText = if (quantityToShow >= 0) quantityToShow.toString() else "—"
            Text(
                text = "Остаток: $qtyText",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (quantityToShow <= 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                fontSize = ProductDimens.CartProductItem.PriceFontSize
            )
        }

        if (product.isInCart) {
            ProductItemQuantitySelector(
                quantity = product.quantity,
                onIncrease = onQuantityIncrease,
                onDecrease = onQuantityDecrease,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .quantitySelectorSize()
            )

            ProductItemRemoveButton(
                onClick = onRemove,
                contentDescriptionRes = R.string.cart_product_remove_from_cart,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        end =
                        ProductDimens.CartProductItem.QuantitySelectorWidth +
                            ProductDimens.CartProductItem.RemoveButtonPadding
                    )
            )
        } else {
            ProductItemAddButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .quantitySelectorSize(),
                onClick = onAddToCart,
                contentDescriptionRes = R.string.cart_product_add_to_cart,
                iconScale = 0.6F
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReplenishProductItemQuantityPreview() {
    ChaikaTheme {
        ReplenishProductItem(
            product = Product(
                id = 2,
                name = "Черный чай",
                description = "Ароматный черный чай",
                image = "",
                price = 85,
                isInCart = true,
                quantity = 3
            ),
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onAddToCart = {},
            onRemove = {},
            packageQuantity = 12
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReplenishProductItemQuantityZeroPreview() {
    ChaikaTheme {
        ReplenishProductItem(
            product = Product(
                id = 2,
                name = "Черный чай",
                description = "Ароматный черный чай",
                image = "",
                price = 85,
                isInCart = false,
                quantity = 0
            ),
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onAddToCart = {},
            onRemove = {},
            packageQuantity = 0
        )
    }
}
