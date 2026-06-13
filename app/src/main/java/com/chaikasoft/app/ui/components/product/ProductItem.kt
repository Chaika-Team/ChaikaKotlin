package com.chaikasoft.app.ui.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.theme.ProductDimens.BackGroundHeight
import com.chaikasoft.app.ui.theme.ProductDimens.ImageHeight
import com.chaikasoft.app.ui.theme.ProductDimens.ImageWidth
import com.chaikasoft.app.ui.theme.ProductDimens.ProductCardHeight
import com.chaikasoft.app.ui.theme.ProductDimens.ProductCardWidth

@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    quantityToShow: Int,
    showQuantityBadge: Boolean = false,
    isSoldOut: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    ConstraintLayout(
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

        Box(
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
                imageUrl = product.image,
                contentDescription = stringResource(
                    id = R.string.product_image_content_description,
                    product.name
                )
            )

            if (showQuantityBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset((-6).dp, (-6).dp) // немного внутрь изображения
                        .size(24.dp)
                        .background(
                            color = colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quantityToShow.toString(),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        ProductContent(
            product = product,
            onAddToCart = onAddToCart,
            onQuantityIncrease = onQuantityIncrease,
            onQuantityDecrease = onQuantityDecrease,
            isSoldOut = isSoldOut,
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
private fun PreviewProductItem() {
    ProductItem(
        product = Product(
            id = 1,
            name = "Black Tea",
            description = "Greenfield",
            image = R.drawable.black_tea.toString(),
            price = 20000,
            isInCart = false,
            quantity = 0
        ),
        modifier = Modifier,
        onAddToCart = {},
        onQuantityIncrease = {},
        onQuantityDecrease = {},
        quantityToShow = 52
    )
}
