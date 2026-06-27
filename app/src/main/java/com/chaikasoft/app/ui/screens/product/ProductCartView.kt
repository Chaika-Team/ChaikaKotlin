package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.ui.components.product.CartPaymentArea
import com.chaikasoft.app.ui.components.product.CartProductItem
import com.chaikasoft.app.ui.components.product.SellResultBottomSheet
import com.chaikasoft.app.ui.mappers.toUiModel
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.viewmodels.ConductorViewModel
import com.chaikasoft.app.ui.viewmodels.SaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCartView(
    saleViewModel: SaleViewModel,
    conductorViewModel: ConductorViewModel,
    navController: NavHostController
) {
    val cartItems by saleViewModel.items.collectAsStateWithLifecycle()
    val conductors by conductorViewModel.allConductors.collectAsStateWithLifecycle()
    val currentConductor by conductorViewModel.conductor.collectAsStateWithLifecycle()
    val stockLimitNotice by saleViewModel.stockLimitNotice.collectAsStateWithLifecycle()
    val sellResultDialog by saleViewModel.sellResultDialog.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedConductor by remember(conductors, currentConductor) {
        mutableStateOf(
            conductors.firstOrNull {
                it.id == currentConductor?.id
            } ?: conductors.firstOrNull()
        )
    }

    val totalCost = cartItems.sumOf { it.product.price * it.quantity }

    StockLimitNoticeEffect(
        stockLimitNotice = stockLimitNotice,
        snackbarHostState = snackbarHostState,
        onShown = saleViewModel::dismissStockLimitNotice
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        ProductCartContent(
            cartItems = cartItems,
            conductors = conductors,
            selectedConductor = selectedConductor,
            totalCost = totalCost,
            onConductorSelected = { selectedConductor = it },
            onQuantityIncrease = { product ->
                saleViewModel.onQuantityChange(
                    product.product.id,
                    product.quantity + 1
                )
            },
            onQuantityDecrease = { product ->
                saleViewModel.onQuantityChange(
                    product.product.id,
                    product.quantity - 1
                )
            },
            onRemove = { product -> saleViewModel.onRemove(product.product.id) },
            onPayCash = {
                selectedConductor?.id?.let { saleViewModel.onSellCash(it) }
            },
            onPayCard = {
                selectedConductor?.id?.let { saleViewModel.onSellCard(it) }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("productCartScreen")
        )
    }
    SellResultBottomSheet(
        messageRes = sellResultDialog?.messageRes,
        onDismiss = saleViewModel::dismissSellResultDialog,
        onConfirm = { navController.navigateUp() }
    )
}

@Composable
private fun ProductCartContent(
    cartItems: List<CartItemDomain>,
    conductors: List<ConductorDomain>,
    selectedConductor: ConductorDomain?,
    totalCost: Int,
    onConductorSelected: (ConductorDomain?) -> Unit,
    onQuantityIncrease: (CartItemDomain) -> Unit,
    onQuantityDecrease: (CartItemDomain) -> Unit,
    onRemove: (CartItemDomain) -> Unit,
    onPayCash: () -> Unit,
    onPayCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(id = R.string.cart_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(ProductDimens.CartPadding)
                ) {
                    items(cartItems, key = { it.product.id }) { product ->
                        CartProductItem(
                            product = product.toUiModel(),
                            onAddToCart = { },
                            onQuantityIncrease = { onQuantityIncrease(product) },
                            onQuantityDecrease = { onQuantityDecrease(product) },
                            onRemove = { onRemove(product) }
                        )
                    }
                }
            }
        }
        CartPaymentArea(
            totalCost = totalCost,
            conductors = conductors,
            selectedConductor = selectedConductor,
            onConductorSelected = onConductorSelected,
            onPayCash = onPayCash,
            onPayCard = onPayCard
        )
    }
}

@Composable
private fun StockLimitNoticeEffect(
    stockLimitNotice: SaleViewModel.StockLimitNotice?,
    snackbarHostState: SnackbarHostState,
    onShown: () -> Unit
) {
    val message = stockLimitNotice?.let { stringResource(id = it.messageRes) }
    LaunchedEffect(stockLimitNotice?.id) {
        val snackbarMessage = message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = snackbarMessage,
            withDismissAction = true
        )
        onShown()
    }
}

@PhoneScalablePreviews
@Composable
private fun ProductCartContentPreview() {
    val conductors = previewConductors()
    ChaikaTheme {
        ProductCartContent(
            cartItems = previewCartItems(),
            conductors = conductors,
            selectedConductor = conductors.first(),
            totalCost = 78_000,
            onConductorSelected = {},
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onRemove = {},
            onPayCash = {},
            onPayCard = {}
        )
    }
}

@PhoneScalablePreviews
@Composable
private fun ProductCartContentEmptyPreview() {
    val conductors = previewConductors()
    ChaikaTheme {
        ProductCartContent(
            cartItems = emptyList(),
            conductors = conductors,
            selectedConductor = conductors.first(),
            totalCost = 0,
            onConductorSelected = {},
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onRemove = {},
            onPayCash = {},
            onPayCard = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun ProductCartContentWidePreview() {
    val conductors = previewConductors()
    ChaikaTheme {
        ProductCartContent(
            cartItems = previewCartItems(),
            conductors = conductors,
            selectedConductor = conductors.first(),
            totalCost = 78_000,
            onConductorSelected = {},
            onQuantityIncrease = {},
            onQuantityDecrease = {},
            onRemove = {},
            onPayCash = {},
            onPayCard = {}
        )
    }
}

private fun previewCartItems(): List<CartItemDomain> = listOf(
    CartItemDomain(
        product = ProductInfoDomain(
            id = 1,
            name = "Чай черный крупнолистовой с очень длинным названием",
            description = "Горячий напиток",
            image = "",
            price = 20_000
        ),
        quantity = 2
    ),
    CartItemDomain(
        product = ProductInfoDomain(
            id = 2,
            name = "Вода негазированная",
            description = "500 мл",
            image = "",
            price = 19_000
        ),
        quantity = 2
    )
)

private fun previewConductors(): List<ConductorDomain> = listOf(
    ConductorDomain(
        id = 1,
        name = "Александр",
        familyName = "Константинопольский",
        givenName = "Владимирович",
        employeeID = "EMP001",
        image = ""
    ),
    ConductorDomain(
        id = 2,
        name = "Анна",
        familyName = "Петрова",
        givenName = "Сергеевна",
        employeeID = "EMP002",
        image = ""
    )
)
