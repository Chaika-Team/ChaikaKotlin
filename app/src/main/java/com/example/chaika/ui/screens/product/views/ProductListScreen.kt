package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.R
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.theme.LightColorScheme
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.navigation.Routes

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.observeCartChanges()
                Lifecycle.Event.ON_STOP -> viewModel.clearState()
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.setCart()
                    navController.navigate(Routes.PRODUCT_CART) {
                        popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cart_buy),
                    contentDescription = "Go to cart"
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
                    text = "Error",
                    color = LightColorScheme.error,
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
                    if (pagingData.itemCount == 0) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "No products available",
                                modifier = Modifier.fillMaxSize().wrapContentSize(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(
                        count = pagingData.itemCount,
                        key = pagingData.itemKey { it.id }
                    ) { index ->
                        val product = pagingData[index]
                        if (product != null) {
                            ProductComponent(
                                product = product,
                                onAddToCart = { viewModel.addToCart(product.id) },
                                onQuantityIncrease = { viewModel.updateQuantity(product.id, +1) },
                                onQuantityDecrease = { viewModel.updateQuantity(product.id, -1) }
                            )
                        }
                    }
                }
            }
        }
    }
}