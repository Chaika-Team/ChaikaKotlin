package com.example.chaika.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.components.template.CheckDialog
import com.example.chaika.ui.viewModels.ConductorViewModel
import com.example.chaika.ui.viewModels.ReplenishItemsViewModel
import com.example.chaika.ui.viewModels.ReplenishViewModel

@Composable
fun ProductReplenishView(
    conductorViewModel: ConductorViewModel,
    replenishViewModel: ReplenishViewModel,
    replenishItemsViewModel: ReplenishItemsViewModel,
    navController: NavHostController,
) {
    val conductor = conductorViewModel.conductor.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val displayProducts = remember(replenishViewModel.items) {
        replenishItemsViewModel.getDisplayProducts(replenishViewModel.items)
    }.collectAsState()
    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                            bottom = 72.dp
                        )
                    ) {
                        items(
                            count = displayProducts.value.size,
                            key = { index -> displayProducts.value[index].id }
                        ) { index ->
                            displayProducts.value[index].let { product ->
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

                ButtonSurface(
                    buttonText = "ДАЛЕЕ",
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                )
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