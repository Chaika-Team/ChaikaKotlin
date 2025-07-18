package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.chaika.ui.components.product.CartPaymentArea
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCartView(
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val conductors by viewModel.conductors.collectAsState()
    val currentConductor by authViewModel.conductorState.collectAsState()

    var selectedConductor by remember(conductors, currentConductor) {
        mutableStateOf(
            conductors.firstOrNull { it.id == currentConductor?.id } ?: conductors.firstOrNull()
        )
    }

    val totalCost = cartItems.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState is ProductViewModel.ScreenState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.cart_loading_error), color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                Box(modifier = Modifier.weight(1f)) {
                    if (cartItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(id = R.string.cart_empty), style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyVerticalGrid (
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(ProductDimens.CartPadding)
                        ) {
                            items(cartItems, key = { it.id }) { product ->
                                CartProductItem(
                                    product = product,
                                    onAddToCart = { },
                                    onQuantityIncrease = { viewModel.changeCartQuantity(product.id, +1) },
                                    onQuantityDecrease = { viewModel.changeCartQuantity(product.id, -1) },
                                    onRemove = { viewModel.removeFromCart(product.id) }
                                )
                            }
                        }
                    }
                }
                CartPaymentArea(
                    totalCost = totalCost,
                    conductors = conductors,
                    selectedConductor = selectedConductor,
                    onConductorSelected = { selectedConductor = it },
                    onPayCash = {
                        selectedConductor?.id?.let { viewModel.payByCash(it) }
                    },
                    onPayCard = {
                        selectedConductor?.id?.let { viewModel.payByCard(it) }
                    }
                )
            }
        }
    }
}