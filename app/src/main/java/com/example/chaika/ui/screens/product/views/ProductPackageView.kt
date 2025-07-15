package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.components.product.CartFAB
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.LightColorScheme
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.util.formatPriceOnly

@Composable
fun ProductPackageView(
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.observeCartChanges()
                else -> {
                    android.util.Log.d("ProductListScreen", "Unhandled lifecycle event: $event")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    val itemsCount = cartItems.sumOf { it.quantity }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = "Error",
                    color = LightColorScheme.error,
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .testTag("packageListGrid")
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(pagingData.itemCount) { index ->
                        val product = pagingData[index]
                        if (product != null) {
                            ProductComponent(
                                modifier = Modifier.testTag("packageCard"),
                                product = product,
                                onAddToCart = {
                                    viewModel.addToCart(product.id)
                                },
                                onQuantityIncrease = {
                                    viewModel.updateCartQuantity(product.id, +1)
                                },
                                onQuantityDecrease = {
                                    viewModel.updateCartQuantity(product.id, -1)
                                },
                            )
                        }
                    }
                }
            }
        }
        CartFAB(
            totalPrice = formatPriceOnly(totalPrice),
            itemsCount = itemsCount,
            onClick = {
                viewModel.setCart()
                navController.navigate(Routes.PRODUCT_CART) {
                    popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 0.dp)
                .offset(y = 8.dp)
        )
    }
}