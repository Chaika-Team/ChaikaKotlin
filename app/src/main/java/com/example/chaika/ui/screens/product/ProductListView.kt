package com.example.chaika.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.chaika.ui.components.product.CartProductItem
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
    val isLoading = productViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        conductorViewModel.refresh()
        productViewModel.loadInitialData(fillViewModel.items)
    }

    DisposableEffect(Unit) {
        productViewModel.loadProducts(fillViewModel.items)
        onDispose { productViewModel.clearProductState() }
    }

    Scaffold(
        floatingActionButton = {
            if (!isLoading.value && pagingItems.itemCount > 0) {
                ArrowFAB(
                    modifier = Modifier.dashedBorder(
                        cornerRadius = ProductDimens.ProductListView.FABCornerRadius
                    ),
                    onClick = {
                        conductor.value?.id?.let { conductorId ->
                            fillViewModel.onAddOperation(conductorId)
                            navController.navigate(Routes.PRODUCT_PACKAGE) {
                                popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Объединенная проверка состояний
            when {
                isLoading.value -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
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
                                pagingItems[index]?.let { product ->
//                                    ProductComponent(
                                    CartProductItem(
                                        modifier = Modifier.testTag("productCard"),
                                        product = product,
                                        onAddToCart = {
                                            fillViewModel.onAdd(product.toCartItemDomain())
                                        },
                                        onQuantityIncrease = {
                                            fillViewModel.onQuantityChange(product.id, product.quantity + 1)
                                        },
                                        onQuantityDecrease = {
                                            fillViewModel.onQuantityChange(product.id, product.quantity - 1)
                                        },
                                        onRemove = {
                                            fillViewModel.onRemove(product.id)
                                        }
                                    )
                                }
                            }

                            // Обработка всех состояний подгрузки
                            when (pagingItems.loadState.append) {
                                is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                                is LoadState.Error -> {
                                    item {
                                        val error = (pagingItems.loadState.append as LoadState.Error).error
                                        ErrorItem(error = error, onRetry = { pagingItems.retry() })
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Ошибка загрузки: ${error.localizedMessage}")
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Composable
private fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Нет доступных товаров")
    }
}

@Composable
private fun ErrorItem(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ошибка подгрузки", color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}