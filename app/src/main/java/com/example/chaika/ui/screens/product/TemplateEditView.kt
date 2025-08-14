package com.example.chaika.ui.screens.product

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.FillViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.mappers.toCartItemDomain
import com.example.chaika.ui.viewModels.ProductViewModel

@Composable
fun TemplateEditView(
    productViewModel: ProductViewModel,
    fillViewModel: FillViewModel,
    navController: NavController,
) {
    val pagingItems = productViewModel.productsFlow.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        productViewModel.loadInitialData(fillViewModel.items)
    }

    DisposableEffect(Unit) {
        productViewModel.loadProducts(fillViewModel.items)
        onDispose { productViewModel.clearProductState() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
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
                        CartProductItem(
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
            }
        }

        ButtonSurface(
            buttonText = "ДАЛЕЕ",
            onClick = {
                navController.navigate(Routes.TEMPLATE_CHECK)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}
