package com.example.chaika.ui.components.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.R
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.theme.ProductDimens.BackGroundHeight
import com.example.chaika.ui.theme.ProductDimens.ImageHeight
import com.example.chaika.ui.theme.ProductDimens.ImageWidth
import com.example.chaika.ui.theme.ProductDimens.ProductCardHeight
import com.example.chaika.ui.theme.ProductDimens.ProductCardWidth


@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    ConstraintLayout (
        modifier = modifier
            .height(ProductCardHeight)
            .width(ProductCardWidth)
    ) {
        val (image, back, content) = createRefs()

        ProductBackground(
            backgroundColor = colorScheme.background,
            cornerRadius = 24.dp,
            modifier = Modifier
                .constrainAs(back) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.matchParent
                    height = Dimension.fillToConstraints
                }
                .height(BackGroundHeight)
                .width(ProductCardWidth)
        )

            Box (
                modifier = Modifier.constrainAs(image) {
                    bottom.linkTo(content.top)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.value(ImageWidth)
                    height = Dimension.value(ImageHeight)
                }
            ) {
                ProductImage(
                    imageUrl = product.image
                )
            }

            ProductContent(
                product = product,
                onAddToCart = onAddToCart,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease,
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(image.bottom)
                    bottom.linkTo(back.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                    .padding(bottom = ProductDimens.PaddingM)
            )

    }
}

@Preview
@Composable
fun PreviewProductItem() {
    ProductItem(
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