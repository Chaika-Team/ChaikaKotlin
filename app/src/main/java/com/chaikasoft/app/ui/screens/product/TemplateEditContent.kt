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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.ui.components.product.CartProductItem
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toCartItemDomain
import com.chaikasoft.app.ui.mappers.toUiModel

@Composable
internal fun ProductScreenContent(
    pagingItems: LazyPagingItems<Product>,
    cartItems: List<CartItemDomain>,
    onNextClick: () -> Unit,
    onAddToCart: (CartItemDomain) -> Unit,
    onQuantityChange: (productId: Int, quantity: Int) -> Unit,
    onRemove: (productId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = pagingItems.loadState.refresh
    val itemCount = pagingItems.itemCount

    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            when {
                refreshState is LoadState.Loading && itemCount == 0 -> {
                    LoadingState()
                }

                refreshState is LoadState.Error && itemCount == 0 -> {
                    ErrorState(
                        error = refreshState.error,
                        onRetry = { pagingItems.retry() }
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

        ButtonSurface(
            buttonText = "ДАЛЕЕ",
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProductList(
    pagingItems: LazyPagingItems<Product>,
    cartItems: List<CartItemDomain>,
    onAddToCart: (CartItemDomain) -> Unit,
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
            key = { index ->
                pagingItems.peek(index)?.id ?: "placeholder_$index"
            }
        ) { index ->
            val product = pagingItems[index]
            if (product != null) {
                val uiProduct =
                    cartMap[product.id]?.toUiModel()
                        ?: product

                CartProductItem(
                    product = uiProduct,
                    onAddToCart = {
                        onAddToCart(product.toCartItemDomain())
                    },
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
                    onRemove = {
                        onRemove(uiProduct.id)
                    }
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
                        onRetry = { pagingItems.retry() }
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
