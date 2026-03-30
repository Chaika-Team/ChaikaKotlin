package com.chaikasoft.app.ui.screens.product

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.components.product.ReplenishProductItem
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.template.CheckDialog
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.ConductorViewModel
import com.chaikasoft.app.ui.viewModels.PackageViewModel
import com.chaikasoft.app.ui.viewModels.ReplenishItemsViewModel
import com.chaikasoft.app.ui.viewModels.ReplenishViewModel

@Composable
fun ProductReplenishView(
    conductorViewModel: ConductorViewModel,
    replenishViewModel: ReplenishViewModel,
    replenishItemsViewModel: ReplenishItemsViewModel,
    packageViewModel: PackageViewModel,
    navController: NavHostController,
) {
    val conductor = conductorViewModel.conductor.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val displayProducts = remember(replenishViewModel.items) {
        replenishItemsViewModel.getDisplayProducts(replenishViewModel.items)
    }.collectAsStateWithLifecycle()
    val productQuantities by packageViewModel.productQuantities.collectAsStateWithLifecycle()
    val checkDialogText = stringResource(id = R.string.template_check_contents)
    val errorNoConductorMsg = stringResource(id = R.string.error_no_conductor)

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
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
                                ReplenishProductItem(
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
                                    },
                                    packageQuantity = productQuantities[product.id] ?: product.quantity
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
                text = checkDialogText,
                onConfirm = {
                    showDialog = false
                    val conductorId = conductor.value?.id
                    if (conductorId != null) {
                        replenishViewModel.onReplenish(conductorId)
                        navController.navigate(Routes.PRODUCT_PACKAGE)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorNoConductorMsg,
                                withDismissAction = true
                            )
                        }
                    }
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}