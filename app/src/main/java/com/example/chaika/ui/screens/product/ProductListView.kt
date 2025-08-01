package com.example.chaika.ui.screens.product

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.components.product.ArrowFAB
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.components.trip.dashedBorder
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.ConductorViewModel
import com.example.chaika.ui.viewModels.FillViewModel
import com.example.chaika.ui.viewModels.ProductViewModel

@Composable
fun ProductListView(
    productViewModel: ProductViewModel,
    conductorViewModel: ConductorViewModel,
    fillViewModel: FillViewModel,
    navController: NavHostController,
) {
    val conductor = conductorViewModel.conductor.collectAsState()
    val pagingItems = productViewModel.productsFlow.collectAsLazyPagingItems()

    LaunchedEffect(pagingItems.loadState) {
        Log.d("ProductListView", "Load state changed: ${pagingItems.loadState}")
        productViewModel.loadInitialData()
        conductorViewModel.refresh()
    }

    DisposableEffect(Unit) {
        productViewModel.loadProducts(fillViewModel.items)

        onDispose {
            productViewModel.clearProductState()
            Log.d("ProductListView", "State cleared on dispose")
        }
    }

    Scaffold(
        floatingActionButton = {
            ArrowFAB(
                modifier = Modifier.dashedBorder(cornerRadius = ProductDimens.ProductListView.FABCornerRadius),
                onClick = {
                    val conductorId = conductor.value?.id
                    Log.i("ProductListView", "Conductor: ${conductor.value}")
                    if (conductorId != null) {
                        Log.i("ProductListView", "Conductor id: $conductorId")
                        fillViewModel.onAddOperation(conductorId)
                        navController.navigate(Routes.PRODUCT_PACKAGE) {
                            popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                        }
                    } else {
                        Log.w("ProductListView", "Conductor ID is null, cannot add to package")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(ProductDimens.ProductListView.GridColumns),
                modifier = Modifier
                    .testTag("productListGrid")
                    .weight(1f),
                contentPadding = PaddingValues(ProductDimens.ProductListView.GridContentPadding)
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index -> pagingItems[index]?.id ?: index }
                ) { index ->
                    val product = pagingItems[index]
                    if (product != null) {
                        ProductComponent(
                            modifier = Modifier.testTag("productCard"),
                            product = product,
                            onAddToCart = {
                                Log.d("ProductListView", "Adding to cart: ${product.name}")
                                fillViewModel.onAdd(product.toCartItemDomain())
                            },
                            onQuantityIncrease = {
                                Log.d("ProductListView", "Increasing quantity for: ${product.name} to ${product.quantity + 1}")
                                fillViewModel.onQuantityChange(product.id, product.quantity + 1)
                            },
                            onQuantityDecrease = {
                                Log.d("ProductListView", "Decreasing quantity for: ${product.name} to ${product.quantity - 1}")
                                fillViewModel.onQuantityChange(product.id, product.quantity - 1)
                            }
                        )
                    } else {
                        Log.w("ProductListView", "Null product at index $index")
                    }
                }
            }

            // Показываем индикатор загрузки дополнительных данных внизу списка
            if (pagingItems.loadState.append is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
