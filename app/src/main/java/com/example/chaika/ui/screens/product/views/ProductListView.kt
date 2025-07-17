package com.example.chaika.ui.screens.product.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.ui.components.product.ArrowFAB
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.theme.LightColorScheme
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.components.trip.dashedBorder
import androidx.compose.ui.res.stringResource
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.R
import com.example.chaika.ui.viewModels.AuthViewModel

@Composable
fun ProductListView(
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.observeCartChanges()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val conductor = authViewModel.conductorState.collectAsState()
    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val packageItems by viewModel.packageItems.collectAsState()

    Scaffold(
        floatingActionButton = {
            ArrowFAB(
                modifier = Modifier.dashedBorder(cornerRadius = ProductDimens.ProductListView.FABCornerRadius),
                onClick = {
                    val conductorId = conductor.value?.id
                    Log.i("ProductListView", "Conductor: ${conductor.value}")
                    if (conductorId != null) {
                        Log.i("ProductListView", "Conductor id: $conductorId")
                        viewModel.addToPackage(conductorId)
                        navController.navigate(Routes.PRODUCT_PACKAGE) {
                            popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            uiState == ProductViewModel.ScreenState.Error -> {
                Text(
                    text = stringResource(id = R.string.error),
                    color = LightColorScheme.error,
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
            else -> {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(ProductDimens.ProductListView.GridColumns),
                        modifier = Modifier
                            .testTag("productListGrid")
                            .weight(1f),
                        contentPadding = PaddingValues(ProductDimens.ProductListView.GridContentPadding)
                    ) {
                        items(
                            count = pagingData.itemCount,
                            key = pagingData.itemKey { it.id }
                        ) { index ->
                            val product = pagingData[index]
                            if (product != null) {
                                val cartItem = cartItems.find { it.id == product.id }
                                val productForDisplay = product.copy(
                                    isInCart = cartItem != null,
                                    quantity = cartItem?.quantity ?: 1,
                                    isInPackage = packageItems.any { it.id == product.id }
                                )
                                ProductComponent(
                                    modifier = Modifier.testTag("productCard"),
                                    product = productForDisplay,
                                    onAddToCart = { viewModel.addToCart(productForDisplay) },
                                    onQuantityIncrease = { viewModel.changeCartQuantity(product.id, +1) },
                                    onQuantityDecrease = { viewModel.changeCartQuantity(product.id, -1) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}