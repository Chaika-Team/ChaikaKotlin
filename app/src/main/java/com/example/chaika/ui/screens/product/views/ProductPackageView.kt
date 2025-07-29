package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chaika.ui.components.product.CartFAB
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.mappers.toProduct
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ReplenishViewModel
import com.example.chaika.ui.viewModels.SaleViewModel
import com.example.chaika.util.formatPriceOnly

@Composable
fun ProductPackageView(
    replenishViewModel: ReplenishViewModel,
    saleViewModel: SaleViewModel,
    navController: NavHostController,
) {
    val packageItems = replenishViewModel.items.collectAsState()
    val cartItems by saleViewModel.items.collectAsState()
    val isLoading = false

    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
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
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .testTag("packageListGrid")
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(packageItems.value, key = { it.product.id }) { item ->
                        val cartItem = cartItems.find { it.product.id == item.product.id }
                        val productForDisplay = item.toProduct()
                        ProductComponent(
                            modifier = Modifier.testTag("packageCard"),
                            product = productForDisplay,
                            onAddToCart = {
                                saleViewModel.onAdd(productForDisplay.toCartItemDomain())
                            },
                            onQuantityIncrease = {
                                saleViewModel.onQuantityChange(productForDisplay.id, (cartItem?.quantity ?: 1) + 1)
                            },
                            onQuantityDecrease = {
                                saleViewModel.onQuantityChange(productForDisplay.id, (cartItem?.quantity ?: 1) - 1)
                            },
                        )
                    }
                }
            }
        }
        CartFAB(
            totalPrice = formatPriceOnly(totalPrice),
            itemsCount = itemsCount,
            onClick = {
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