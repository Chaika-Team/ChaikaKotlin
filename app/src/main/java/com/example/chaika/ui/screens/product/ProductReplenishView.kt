package com.example.chaika.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.components.template.CheckDialog
import com.example.chaika.ui.viewModels.ConductorViewModel
import com.example.chaika.ui.viewModels.ReplenishViewModel

@Composable
fun ProductReplenishView(
    productViewModel: ProductViewModel,
    conductorViewModel: ConductorViewModel,
    replenishViewModel: ReplenishViewModel,
    navController: NavHostController,
) {
    val conductor = conductorViewModel.conductor.collectAsState()
    val pagingItems = productViewModel.productsFlow.collectAsLazyPagingItems()
    val isLoading = productViewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.loadInitialData(replenishViewModel.items)
    }

    DisposableEffect(Unit) {
        productViewModel.loadProducts(replenishViewModel.items)
        onDispose { productViewModel.clearProductState() }
    }

    Scaffold { innerPadding ->
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
                        Box(modifier = Modifier.weight(1f)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                modifier = Modifier
                                    .testTag("productListGrid"),
                                contentPadding = PaddingValues(
                                    bottom = 72.dp // Добавляем отступ снизу для кнопки
                                )
                            ) {
                                items(
                                    count = pagingItems.itemCount,
                                    key = { index -> pagingItems[index]?.id ?: index }
                                ) { index ->
                                    pagingItems[index]?.let { product ->
                                        CartProductItem(
                                            modifier = Modifier.testTag("productCard"),
                                            product = product,
                                            onAddToCart = {
                                                replenishViewModel.onAdd(product.toCartItemDomain())
                                            },
                                            onQuantityIncrease = {
                                                replenishViewModel.onQuantityChange(
                                                    product.id,
                                                    product.quantity + 1
                                                )
                                            },
                                            onQuantityDecrease = {
                                                replenishViewModel.onQuantityChange(
                                                    product.id,
                                                    product.quantity - 1
                                                )
                                            },
                                            onRemove = {
                                                replenishViewModel.onRemove(product.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Кнопка "ДАЛЕЕ" внизу экрана
                        ButtonSurface(
                            buttonText = "ДАЛЕЕ",
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (showDialog) {
            CheckDialog(
                text = "Вы уверены?\nПожалуйста, проверьте содержимое пакета",
                onConfirm = {
                    showDialog = false
                    val conductorId = conductor.value?.id
                    if (conductorId != null) {
                        replenishViewModel.onReplenish(conductorId)
                        navController.navigate(Routes.PRODUCT_PACKAGE)
                    } else {
                        // TODO: показать ошибку (нет проводника) и не навигировать
                    }
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}