package com.example.chaika.ui.components.product

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.chaika.ui.dto.Product
import androidx.compose.runtime.*


@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    val currentProduct by rememberUpdatedState(newValue = product)

    Box(
        modifier = modifier
            .width(160.dp)
            .height(260.dp)
    ) {
        ProductBackground(
            isInCart = currentProduct.isInCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .align(Alignment.BottomCenter)
        )

        ProductContent(
            product = currentProduct,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 70.dp),
            onAddToCart = onAddToCart,
            onQuantityIncrease = onQuantityIncrease,
            onQuantityDecrease = onQuantityDecrease,
        )

        ProductImage(
            imageUrl = currentProduct.image,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopCenter)
                .offset(y = 20.dp)
                .zIndex(1f)
        )
    }
}