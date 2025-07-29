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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.chaika.ui.components.product.CartPaymentArea
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.R
import com.example.chaika.ui.mappers.toUiModel
import com.example.chaika.ui.viewModels.SaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCartView(
    saleViewModel: SaleViewModel,
    authViewModel: AuthViewModel
) {
    val cartItems by saleViewModel.items.collectAsState()
    val conductors by authViewModel.conductors.collectAsState()
    val currentConductor by authViewModel.conductorState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    authViewModel.loadConductors()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var selectedConductor by remember(conductors, currentConductor) {
        mutableStateOf(
            conductors.firstOrNull { it.id == currentConductor?.id } ?: conductors.firstOrNull()
        )
    }

    val totalCost = cartItems.sumOf { it.product.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                    items(cartItems, key = { it.product.id }) { product ->
                        CartProductItem(
                            product = product.product.toUiModel(),
                            onAddToCart = { },
                            onQuantityIncrease = { saleViewModel.onQuantityChange(product.product.id, product.quantity+1) },
                            onQuantityDecrease = { saleViewModel.onQuantityChange(product.product.id, product.quantity-1) },
                            onRemove = { saleViewModel.onRemove(product.product.id) }
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
                selectedConductor?.id?.let { saleViewModel.onSellCash(it) }
            },
            onPayCard = {
                selectedConductor?.id?.let { saleViewModel.onSellCard(it) }
            }
        )
    }
}