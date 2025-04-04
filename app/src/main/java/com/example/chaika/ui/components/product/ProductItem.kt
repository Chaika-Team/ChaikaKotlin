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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .height(130.dp)
            .width(165.dp)
    ) {
        // 1. Фон
        ProductBackground(
            modifier = Modifier.matchParentSize(),
            backgroundColor = Color.White,
            cornerRadius = 24.dp
        )

        // 2. Изображение - центрируем через Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.TopCenter
        // Выравнивание по центру сверху
        ) {
            ProductImage(
                imageUrl = product.image,
                modifier = Modifier
                    .size(122.dp)
                    .offset(y = (-80).dp) // Меньший отступ для корректного позиционирования
            )
        }

        // 3. Контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            ProductContent(
                product = product,
                modifier = Modifier.fillMaxWidth(),
                onAddToCart = onAddToCart,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease,
            )
        }
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
            image = "res/drawable/black_tea.jpeg",
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