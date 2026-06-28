package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.ConductorViewModel
import com.chaikasoft.app.ui.viewmodels.PackageViewModel
import com.chaikasoft.app.ui.viewmodels.ReplenishItemsViewModel
import com.chaikasoft.app.ui.viewmodels.ReplenishViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductReplenishView(
    conductorViewModel: ConductorViewModel,
    replenishViewModel: ReplenishViewModel,
    replenishItemsViewModel: ReplenishItemsViewModel,
    packageViewModel: PackageViewModel,
    navController: NavHostController
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        ProductReplenishContent(
            products = displayProducts.value,
            productQuantities = productQuantities,
            onAdd = { replenishViewModel.onAdd(it.toCartItemDomain()) },
            onQuantityChange = replenishViewModel::onQuantityChange,
            onRemove = replenishViewModel::onRemove,
            onNextClick = { showDialog = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

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

@Composable
private fun ProductReplenishContent(
    products: List<Product>,
    productQuantities: Map<Int, Int>,
    onAdd: (Product) -> Unit,
    onQuantityChange: (productId: Int, quantity: Int) -> Unit,
    onRemove: (productId: Int) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.testTag("productListGrid"),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(
                    count = products.size,
                    key = { index -> products[index].id }
                ) { index ->
                    val product = products[index]
                    ReplenishProductItem(
                        modifier = Modifier.testTag("productCard"),
                        product = product,
                        onAddToCart = { onAdd(product) },
                        onQuantityIncrease = {
                            onQuantityChange(
                                product.id,
                                product.quantity + 1
                            )
                        },
                        onQuantityDecrease = {
                            onQuantityChange(
                                product.id,
                                product.quantity - 1
                            )
                        },
                        onRemove = { onRemove(product.id) },
                        packageQuantity = productQuantities[product.id] ?: product.quantity
                    )
                }
            }
        }

        ButtonSurface(
            buttonText = "ДАЛЕЕ",
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PhoneScalablePreviews
@Composable
private fun ProductReplenishContentPreview() {
    ChaikaTheme {
        ProductReplenishContent(
            products = previewReplenishProducts(),
            productQuantities = mapOf(1 to 12, 2 to 0),
            onAdd = {},
            onQuantityChange = { _, _ -> },
            onRemove = {},
            onNextClick = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun ProductReplenishContentWidePreview() {
    ChaikaTheme {
        ProductReplenishContent(
            products = previewReplenishProducts(),
            productQuantities = mapOf(1 to 12, 2 to 0),
            onAdd = {},
            onQuantityChange = { _, _ -> },
            onRemove = {},
            onNextClick = {}
        )
    }
}

private fun previewReplenishProducts(): List<Product> = listOf(
    Product(
        id = 1,
        name = "Чай черный крупнолистовой с очень длинным названием",
        description = "Горячий напиток",
        image = "",
        price = 20_000,
        isInCart = true,
        quantity = 2
    ),
    Product(
        id = 2,
        name = "Вода негазированная",
        description = "500 мл",
        image = "",
        price = 19_000,
        isInCart = false,
        quantity = 0
    )
)
