package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chaika.R
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.theme.LightColorScheme
import com.example.chaika.ui.viewModels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCartView(
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cart_buy),
                    contentDescription = "Proceed to checkout"
                )
            }
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            uiState == ProductViewModel.ScreenState.Error -> {
                Text(
                    text = "Error loading cart",
                    color = LightColorScheme.error,
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
            cartItems.isEmpty() -> {
                Text(
                    text = "Your cart is empty",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        count = cartItems.size,
                        key = { index -> cartItems[index].id }
                    ) { index ->
                        val product = cartItems[index]
                        ProductComponent(
                            product = product,
                            onAddToCart = { viewModel.addToCart(product.id) },
                            onQuantityIncrease = { viewModel.updateCartQuantity(product.id, +1) },
                            onQuantityDecrease = { viewModel.updateCartQuantity(product.id, -1) },
                        )
                    }
                }
            }
        }
    }
}