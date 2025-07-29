package com.example.chaika.ui.components.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.theme.ProductDimens.PaddingL
import com.example.chaika.ui.theme.ProductDimens.QuantitySelectorHeight
import com.example.chaika.util.formatPriceOnly


@Composable
fun ProductContent(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingL)
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        AnimatedVisibility(visible = !product.isInCart) {
            NotInCartContent(
                price = product.price,
                description = product.description,
                onAddToCart = onAddToCart
            )
        }
        AnimatedVisibility(visible = product.isInCart) {
            InCartContent(
                price = product.price,
                quantity = product.quantity,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotInCartContent(
    price: Double,
    description: String,
    onAddToCart: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = description,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = formatPriceOnly(price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            modifier = Modifier.size(QuantitySelectorHeight),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primary),
            onClick = onAddToCart,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to cart",
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.75F)
            )
        }
    }
}

@Composable
private fun InCartContent(
    price: Double,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    Column {
        Text(
            text = formatPriceOnly(price),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        QuantitySelector(
            quantity = quantity,
            onIncrease = onQuantityIncrease,
            onDecrease = onQuantityDecrease
        )
    }
}