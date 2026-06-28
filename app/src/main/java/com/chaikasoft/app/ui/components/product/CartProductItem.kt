package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import com.chaikasoft.app.util.formatPrice

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
    ProductListItemLayout(
        product = product,
        modifier = modifier
    ) { actionRowModifier ->
        CartProductActionsRow(
            product = product,
            onQuantityIncrease = onQuantityIncrease,
            onQuantityDecrease = onQuantityDecrease,
            onAddToCart = onAddToCart,
            onRemove = onRemove,
            onRemoveFromPackage = onRemoveFromPackage,
            modifier = actionRowModifier
        )
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
            .heightIn(min = ProductDimens.CartProductItem.QuantitySelectorHeight)
    ) {
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
        } else {
            ProductItemAddButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .quantitySelectorSize(),
                onClick = onAddToCart,
                contentDescriptionRes = R.string.cart_product_add_to_cart
            )
        }

        if (product.isInCart) {
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
        } else if (product.isInCart && onRemoveFromPackage != null) {
            ProductItemRemoveButton(
                onClick = onRemoveFromPackage,
                contentDescriptionRes = R.string.cart_product_remove_from_package,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(
                        x =
                        (-ProductDimens.CartProductItem.QuantitySelectorWidth) -
                            ProductDimens.CartProductItem.RemoveButtonPadding
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartProductItemPreview() {
    ChaikaTheme {
        CartProductItem(
            product = Product(
                id = 1,
                name = "Яблочный сок",
                description = "Свежий яблочный сок",
                image = "",
                price = 120,
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
private fun CartProductItemNotInCartPreview() {
    ChaikaTheme {
        CartProductItem(
            product = Product(
                id = 2,
                name = "Черный чай",
                description = "Ароматный черный чай",
                image = "",
                price = 85,
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
