package com.chaikasoft.app.ui.screens.product

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.ui.components.product.CartFAB
import com.chaikasoft.app.ui.components.product.ProductComponent
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.viewmodels.PackageViewModel
import com.chaikasoft.app.ui.viewmodels.SaleViewModel
import com.chaikasoft.app.util.formatPriceOnly

@Composable
fun ProductPackageView(
    packageViewModel: PackageViewModel,
    saleViewModel: SaleViewModel,
    navController: NavHostController
) {
    val packageItems by packageViewModel.productsFlow.collectAsStateWithLifecycle()
    val cartItems by saleViewModel.items.collectAsStateWithLifecycle()
    val productQuantities by packageViewModel.productQuantities.collectAsStateWithLifecycle()
    val soldOutNotice by saleViewModel.soldOutNotice.collectAsStateWithLifecycle()
    val stockLimitNotice by saleViewModel.stockLimitNotice.collectAsStateWithLifecycle()
    val isLoading by packageViewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        packageViewModel.loadProducts(saleViewModel.items)
        packageViewModel.refreshAllQuantities()
    }

    SoldOutNoticeEffect(
        soldOutNotice = soldOutNotice,
        snackbarHostState = snackbarHostState,
        onBeforeShow = packageViewModel::refreshAllQuantities,
        onShown = saleViewModel::dismissSoldOutNotice
    )
    StockLimitNoticeEffect(
        stockLimitNotice = stockLimitNotice,
        snackbarHostState = snackbarHostState,
        onBeforeShow = packageViewModel::refreshAllQuantities,
        onShown = saleViewModel::dismissStockLimitNotice
    )

    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
    val itemsCount = cartItems.sumOf { it.quantity }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("productPackageScreen")
        ) {
            ProductPackageContent(
                isLoading = isLoading,
                packageItems = packageItems,
                cartItems = cartItems,
                productQuantities = productQuantities,
                onCheckProductQuantity = packageViewModel::checkProductQuantity,
                onAdd = saleViewModel::onAdd,
                onQuantityChange = saleViewModel::onQuantityChange,
                modifier = Modifier.fillMaxSize()
            )
            CartFAB(
                totalPrice = formatPriceOnly(totalPrice),
                itemsCount = itemsCount,
                onClick = {
                    navController.navigate(Routes.PRODUCT_CART)
                },
                tag = "productCartFab",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 0.dp)
                    .offset(y = 8.dp)
            )
        }
    }
}

@Composable
private fun StockLimitNoticeEffect(
    stockLimitNotice: SaleViewModel.StockLimitNotice?,
    snackbarHostState: SnackbarHostState,
    onBeforeShow: () -> Unit,
    onShown: () -> Unit
) {
    val message = stockLimitNotice?.let { stringResource(id = it.messageRes) }
    LaunchedEffect(stockLimitNotice?.id) {
        val snackbarMessage = message ?: return@LaunchedEffect
        onBeforeShow()
        snackbarHostState.showSnackbar(
            message = snackbarMessage,
            withDismissAction = true
        )
        onShown()
    }
}

@Composable
private fun SoldOutNoticeEffect(
    soldOutNotice: SaleViewModel.SoldOutNotice?,
    snackbarHostState: SnackbarHostState,
    onBeforeShow: () -> Unit,
    onShown: () -> Unit
) {
    val soldOutMessage = soldOutNotice?.let { notice ->
        stringResource(
            id = R.string.sold_out_products_message,
            notice.productNames.joinToString()
        )
    }
    LaunchedEffect(soldOutNotice?.id) {
        val message = soldOutMessage ?: return@LaunchedEffect
        onBeforeShow()
        snackbarHostState.showSnackbar(
            message = message,
            withDismissAction = true
        )
        onShown()
    }
}

@Composable
private fun ProductPackageContent(
    isLoading: Boolean,
    packageItems: List<Product>,
    cartItems: List<CartItemDomain>,
    productQuantities: Map<Int, Int>,
    onCheckProductQuantity: (Int) -> Unit,
    onAdd: (CartItemDomain) -> Unit,
    onQuantityChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize(),
            color = MaterialTheme.colorScheme.primary
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .testTag("packageListGrid")
            .fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = ProductDimens.ProductCardHeight
        )
    ) {
        items(packageItems, key = { it.id }) { item ->
            PackageProductCard(
                item = item,
                cartItem = cartItems.find { it.product.id == item.id },
                productQuantities = productQuantities,
                onCheckProductQuantity = onCheckProductQuantity,
                onAdd = onAdd,
                onQuantityChange = onQuantityChange
            )
        }
    }
}

@Composable
private fun PackageProductCard(
    item: Product,
    cartItem: CartItemDomain?,
    productQuantities: Map<Int, Int>,
    onCheckProductQuantity: (Int) -> Unit,
    onAdd: (CartItemDomain) -> Unit,
    onQuantityChange: (Int, Int) -> Unit
) {
    LaunchedEffect(item.id) {
        if (!productQuantities.containsKey(item.id)) {
            onCheckProductQuantity(item.id)
        }
    }

    val hasKnownQuantity = productQuantities.containsKey(item.id)
    val quantityToShow = if (hasKnownQuantity) {
        productQuantities.getValue(item.id)
    } else {
        cartItem?.quantity ?: 0
    }
    val hasQuantityToShow = hasKnownQuantity || cartItem != null

    ProductComponent(
        modifier = Modifier.testTag("packageCard"),
        product = item,
        onAddToCart = { onAdd(item.toCartItemDomain()) },
        onQuantityIncrease = {
            onQuantityChange(
                item.id,
                (cartItem?.quantity ?: 1) + 1
            )
        },
        onQuantityDecrease = {
            onQuantityChange(
                item.id,
                (cartItem?.quantity ?: 1) - 1
            )
        },
        showQuantityBadge = hasQuantityToShow,
        quantityToShow = quantityToShow,
        isSoldOut = hasKnownQuantity && quantityToShow <= 0
    )
}

@PhoneScalablePreviews
@Composable
private fun ProductPackageViewPreview() {
    val products = previewPackageProducts()
    ChaikaTheme {
        ProductPackageContent(
            isLoading = false,
            packageItems = products,
            cartItems = emptyList(),
            productQuantities = products.associate { it.id to 5 },
            onCheckProductQuantity = {},
            onAdd = {},
            onQuantityChange = { _, _ -> }
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun ProductPackageWidePreview() {
    val products = previewPackageProducts()
    ChaikaTheme {
        ProductPackageContent(
            isLoading = false,
            packageItems = products,
            cartItems = emptyList(),
            productQuantities = products.associate { it.id to 5 },
            onCheckProductQuantity = {},
            onAdd = {},
            onQuantityChange = { _, _ -> }
        )
    }
}

private fun previewPackageProducts(): List<Product> = listOf(
    Product(
        id = 1,
        name = "Black tea",
        description = "Classic",
        image = R.drawable.black_tea.toString(),
        price = 20_000,
        isInCart = false,
        quantity = 0
    ),
    Product(
        id = 2,
        name = "Green tea",
        description = "Jasmine",
        image = R.drawable.black_tea.toString(),
        price = 22_000,
        isInCart = true,
        quantity = 2
    ),
    Product(
        id = 3,
        name = "Coffee",
        description = "Arabica",
        image = R.drawable.black_tea.toString(),
        price = 35_000,
        isInCart = false,
        quantity = 0
    ),
    Product(
        id = 4,
        name = "Water",
        description = "Still",
        image = R.drawable.black_tea.toString(),
        price = 10_000,
        isInCart = false,
        quantity = 0
    ),
    Product(
        id = 5,
        name = "Apple juice",
        description = "Fresh",
        image = R.drawable.black_tea.toString(),
        price = 18_000,
        isInCart = false,
        quantity = 0
    ),
    Product(
        id = 6,
        name = "Cocoa",
        description = "Classic",
        image = R.drawable.black_tea.toString(),
        price = 25_000,
        isInCart = false,
        quantity = 0
    )
)
