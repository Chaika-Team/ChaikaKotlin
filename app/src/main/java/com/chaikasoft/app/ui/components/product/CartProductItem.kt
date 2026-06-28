package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ProductDimens.CartProductItem.QuantitySelectorHeight)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = ProductDimens.CartProductItem.RemoveButtonPadding)
        ) {
            Text(
                text = formatPrice(product.price, product.quantity),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = ProductDimens.CartProductItem.PriceFontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (product.isInCart) {
            ProductItemRemoveButton(
                onClick = onRemoveFromPackage ?: onRemove,
                contentDescriptionRes = if (onRemoveFromPackage != null) {
                    R.string.cart_product_remove_from_package
                } else {
                    R.string.cart_product_remove_from_cart
                }
            )
            Spacer(modifier = Modifier.width(ProductDimens.CartProductItem.RemoveButtonPadding))
            ProductItemQuantitySelector(
                quantity = product.quantity,
                onIncrease = onQuantityIncrease,
                onDecrease = onQuantityDecrease,
                modifier = Modifier.quantitySelectorSize()
            )
        } else {
            ProductItemAddButton(
                modifier = Modifier.quantitySelectorSize(),
                onClick = onAddToCart,
                contentDescriptionRes = R.string.cart_product_add_to_cart
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
