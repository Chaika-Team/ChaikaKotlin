package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.theme.ProductDimens.PaddingM

@Composable
fun ProductComponent(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    val height = ProductDimens.ProductCardHeight + (2 * PaddingM.value).dp
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(height).padding(PaddingM)
    ) {
        ProductItem(
            product = product,
            modifier = modifier,
            onAddToCart = onAddToCart,
            onQuantityIncrease = onQuantityIncrease,
            onQuantityDecrease = onQuantityDecrease
        )
    }
}

@Preview
@Composable
fun PreviewProductComponent() {
    ProductComponent(
        product = Product(
            id = 1,
            name = "Black Tea",
            description = "Greenfield",
            image = R.drawable.black_tea.toString(),
            price = 20000.0,
            isInCart = false,
            quantity = 0
        ),
        modifier = Modifier,
        onAddToCart = {},
        onQuantityIncrease = {},
        onQuantityDecrease = {}
    )
}