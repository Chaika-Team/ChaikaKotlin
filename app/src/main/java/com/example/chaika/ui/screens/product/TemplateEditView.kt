package com.example.chaika.ui.screens.product

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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.FillViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.components.template.CheckDialog
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.viewModels.ConductorViewModel

@Composable
fun TemplateEditView(
    productViewModel: ProductViewModel,
    conductorViewModel: ConductorViewModel,
    fillViewModel: FillViewModel,
    navController: NavHostController,
) {
    val pagingItems = productViewModel.productsFlow.collectAsLazyPagingItems()
    val isSyncing by productViewModel.isSyncing.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.loadInitialData(fillViewModel.items)
    }

    DisposableEffect(Unit) {
        onDispose {
            productViewModel.clearProductState()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Индикатор фоновой синхронизации
            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Основной контент с обработкой LoadState
            when (val refreshState = pagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    // Показываем центральный прогресс только при первой загрузке
                    // и отсутствии кешированных данных
                    if (pagingItems.itemCount == 0) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Если есть данные, показываем список
                        ProductContent(
                            pagingItems = pagingItems,
                            fillViewModel = fillViewModel,
                            onShowDialog = { showDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is LoadState.Error -> {
                    // Показываем ошибку только если нет кешированных данных
                    if (pagingItems.itemCount == 0) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                error = refreshState.error,
                                onRetry = { pagingItems.retry() }
                            )
                        }
                    } else {
                        // Если есть данные, показываем список
                        ProductContent(
                            pagingItems = pagingItems,
                            fillViewModel = fillViewModel,
                            onShowDialog = { showDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    ProductContent(
                        pagingItems = pagingItems,
                        fillViewModel = fillViewModel,
                        onShowDialog = { showDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showDialog) {
            CheckDialog(
                text = "Вы уверены?\nПожалуйста, проверьте содержимое пакета",
                onConfirm = {
                    showDialog = false
                    val conductorId = conductorViewModel.conductor.value?.id
                    if (conductorId != null) {
                        fillViewModel.onAddOperation(conductorId)
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

@Composable
private fun ProductContent(
    pagingItems: LazyPagingItems<Product>,
    fillViewModel: FillViewModel,
    onShowDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.testTag("productListGrid"),
                contentPadding = PaddingValues(bottom = 72.dp)
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
                                fillViewModel.onAdd(product.toCartItemDomain())
                            },
                            onQuantityIncrease = {
                                fillViewModel.onQuantityChange(
                                    product.id,
                                    product.quantity + 1
                                )
                            },
                            onQuantityDecrease = {
                                fillViewModel.onQuantityChange(
                                    product.id,
                                    product.quantity - 1
                                )
                            },
                            onRemove = {
                                fillViewModel.onRemove(product.id)
                            }
                        )
                    }
                }

                // Обработка состояния загрузки следующих страниц
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
                            ErrorItem(
                                error = error,
                                onRetry = { pagingItems.retry() }
                            )
                        }
                    }

                    else -> {}
                }
            }
        }

        // Кнопка "ДАЛЕЕ" внизу экрана
        ButtonSurface(
            buttonText = "ДАЛЕЕ",
            onClick = onShowDialog,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ErrorItem(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ошибка подгрузки",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = error.message ?: "Неизвестная ошибка",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}