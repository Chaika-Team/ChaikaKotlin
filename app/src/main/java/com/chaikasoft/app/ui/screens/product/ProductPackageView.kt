package com.chaikasoft.app.ui.screens.product

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.ui.components.product.CartFAB
import com.chaikasoft.app.ui.components.product.ProductComponent
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.theme.ProductDimens.PaddingM
import com.chaikasoft.app.ui.viewModels.PackageViewModel
import com.chaikasoft.app.ui.viewModels.SaleViewModel
import com.chaikasoft.app.util.formatPriceOnly
import androidx.compose.foundation.lazy.grid.GridItemSpan

@Composable
fun ProductPackageView(
    packageViewModel: PackageViewModel,
    saleViewModel: SaleViewModel,
    navController: NavHostController,
) {
    val packageItems = packageViewModel.productsFlow.collectAsStateWithLifecycle()
    val cartItems by saleViewModel.items.collectAsStateWithLifecycle()
    val spacerHeight = (ProductDimens.ProductCardHeight.value / 2).dp
    val isLoading = false

    DisposableEffect(Unit) {
        packageViewModel.loadProducts(saleViewModel.items)

        onDispose {
            packageViewModel.clearProductState()
            Log.d("ProductListView", "State cleared on dispose")
        }
    }

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
                    items(packageItems.value, key = { it.id }) { item ->
                        val cartItem = cartItems.find { it.product.id == item.id }
                        ProductComponent(
                            modifier = Modifier.testTag("packageCard"),
                            product = item,
                            onAddToCart = {
                                saleViewModel.onAdd(item.toCartItemDomain())
                            },
                            onQuantityIncrease = {
                                saleViewModel.onQuantityChange(item.id, (cartItem?.quantity ?: 1) + 1)
                            },
                            onQuantityDecrease = {
                                saleViewModel.onQuantityChange(item.id, (cartItem?.quantity ?: 1) - 1)
                            },
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(spacerHeight))
                    }
                }
            }
        }
        CartFAB(
            totalPrice = formatPriceOnly(totalPrice),
            itemsCount = itemsCount,
            onClick = {
                navController.navigate(Routes.PRODUCT_CART)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 0.dp)
                .offset(y = 8.dp)
        )
    }
}