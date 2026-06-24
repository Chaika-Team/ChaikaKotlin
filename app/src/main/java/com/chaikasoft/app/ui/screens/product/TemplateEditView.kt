package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.ui.components.product.CartProductItem
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.template.CheckDialog
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.mappers.toUiModel
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.ProductDimens
import com.chaikasoft.app.ui.viewmodels.ConductorViewModel
import com.chaikasoft.app.ui.viewmodels.FillViewModel
import com.chaikasoft.app.ui.viewmodels.ProductViewModel

@Composable
fun TemplateEditView(
    productViewModel: ProductViewModel,
    conductorViewModel: ConductorViewModel,
    fillViewModel: FillViewModel,
    navController: NavHostController
) {
    val pagingItems = productViewModel.productsFlow.collectAsLazyPagingItems()
    val cartItems by fillViewModel.items.collectAsStateWithLifecycle()
    val searchQuery by productViewModel.searchQuery.collectAsStateWithLifecycle()

    var showNoConductorError by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->

        TemplateEditContent(
            searchQuery = searchQuery,
            onSearchChange = productViewModel::onSearchChange,
            onNextClick = {
                val conductorId = conductorViewModel.conductor.value?.id
                if (conductorId == null) {
                    showNoConductorError = true
                } else {
                    navController.navigate(Routes.TEMPLATE_CONFIRM)
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            ProductPagingContent(
                pagingItems = pagingItems,
                cartItems = cartItems,
                onAddToCart = { product ->
                    fillViewModel.onAdd(product.toCartItemDomain())
                },
                onQuantityChange = fillViewModel::onQuantityChange,
                onRemove = fillViewModel::onRemove,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Диалог отсутствия проводника
        if (showNoConductorError) {
            CheckDialog(
                text = "Ошибка: проводник не выбран.\nПожалуйста, выберите проводника.",
                onConfirm = {
                    showNoConductorError = false
                    navController.popBackStack()
                },
                onDismiss = { showNoConductorError = false }
            )
        }
    }
}

@Composable
private fun TemplateEditContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    productContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            placeholder = { Text(stringResource(R.string.hint_product_search)) },
            singleLine = true,
            shape = RoundedCornerShape(ProductDimens.CornerRadiusM)
        )

        Box(modifier = Modifier.weight(1f)) {
            productContent()
        }

        ButtonSurface(
            buttonText = stringResource(R.string.action_next),
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProductPagingContent(
    pagingItems: LazyPagingItems<Product>,
    cartItems: List<CartItemDomain>,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (productId: Int, quantity: Int) -> Unit,
    onRemove: (productId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = pagingItems.loadState.refresh
    val itemCount = pagingItems.itemCount

    Box(modifier = modifier) {
        when {
            refreshState is LoadState.Loading && itemCount == 0 -> {
                LoadingState()
            }

            refreshState is LoadState.Error && itemCount == 0 -> {
                ErrorState(
                    error = refreshState.error,
                    onRetry = pagingItems::retry
                )
            }

            refreshState is LoadState.NotLoading && itemCount == 0 -> {
                EmptyState()
            }

            else -> {
                ProductList(
                    pagingItems = pagingItems,
                    cartItems = cartItems,
                    onAddToCart = onAddToCart,
                    onQuantityChange = onQuantityChange,
                    onRemove = onRemove
                )
            }
        }
    }
}

@Composable
private fun ProductList(
    pagingItems: LazyPagingItems<Product>,
    cartItems: List<CartItemDomain>,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (productId: Int, quantity: Int) -> Unit,
    onRemove: (productId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartMap = remember(cartItems) {
        cartItems
            .filter { it.quantity > 0 }
            .associateBy { it.product.id }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.testTag("productListGrid"),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { product -> product.id }
        ) { index ->
            val product = pagingItems[index]
            if (product != null) {
                val uiProduct =
                    cartMap[product.id]?.toUiModel()
                        ?: product

                CartProductItem(
                    product = uiProduct,
                    onAddToCart = { onAddToCart(product) },
                    onQuantityIncrease = {
                        onQuantityChange(
                            uiProduct.id,
                            uiProduct.quantity + 1
                        )
                    },
                    onQuantityDecrease = {
                        onQuantityChange(
                            uiProduct.id,
                            uiProduct.quantity - 1
                        )
                    },
                    onRemove = { onRemove(uiProduct.id) }
                )
            }
        }

        item {
            when (val appendState = pagingItems.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                is LoadState.Error -> {
                    ErrorItem(
                        error = appendState.error,
                        onRetry = pagingItems::retry
                    )
                }

                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Товары не найдены",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Попробуйте изменить поисковый запрос",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ErrorState(error: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ErrorItem(
            error = error,
            onRetry = onRetry
        )
    }
}

@Composable
private fun ErrorItem(error: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ошибка загрузки",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = error.localizedMessage ?: "Неизвестная ошибка",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun TemplateEditContentPreview() {
    val products = templatePreviewProducts()

    ChaikaTheme {
        TemplateEditContent(
            searchQuery = "",
            onSearchChange = {},
            onNextClick = {}
        ) {
            TemplateEditPreviewProductList(products = products)
        }
    }
}

@Composable
private fun TemplateEditPreviewProductList(products: List<Product>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        items(
            count = products.size,
            key = { index -> products[index].id }
        ) { index ->
            CartProductItem(
                product = products[index],
                onAddToCart = {},
                onQuantityIncrease = {},
                onQuantityDecrease = {},
                onRemove = {}
            )
        }
    }
}

private fun templatePreviewProducts() = listOf(
    Product(
        id = 1,
        name = "Яблочный сок",
        description = "Свежий яблочный сок",
        image = "",
        price = 120,
        isInCart = true,
        quantity = 2
    ),
    Product(
        id = 2,
        name = "Чёрный чай",
        description = "Ароматный чёрный чай",
        image = "",
        price = 85,
        isInCart = false,
        quantity = 1
    )
)
