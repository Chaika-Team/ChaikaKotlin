package com.example.chaika.ui.screens.product.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.example.chaika.ui.components.product.ArrowFAB
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.components.trip.dashedBorder
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.viewModels.FillViewModel
import com.example.chaika.ui.mappers.toUiModel

@Composable
fun ProductListView(
    fillViewModel: FillViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    val conductor = authViewModel.conductorState.collectAsState()
    val cartItems by fillViewModel.items.collectAsState()

    Scaffold(
        floatingActionButton = {
            ArrowFAB(
                modifier = Modifier.dashedBorder(cornerRadius = ProductDimens.ProductListView.FABCornerRadius),
                onClick = {
                    val conductorId = conductor.value?.id
                    Log.i("ProductListView", "Conductor: ${conductor.value}")
                    if (conductorId != null) {
                        Log.i("ProductListView", "Conductor id: $conductorId")
                        fillViewModel.onAddOperation(conductorId)
                        navController.navigate(Routes.PRODUCT_PACKAGE) {
                            popUpTo(Routes.PRODUCT_LIST) { inclusive = false }
                        }
                    } else {
                        Log.w("ProductListView", "Conductor ID is null, cannot add to package")
                    }
                }
            )
        }
    ) { innerPadding ->
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
                items(cartItems.size) { index ->
                    val cartItem = cartItems[index]
                    ProductComponent(
                        modifier = Modifier.testTag("productCard"),
                        product = cartItem.product.toUiModel(),
                        onAddToCart = { fillViewModel.onAdd(cartItem) },
                        onQuantityIncrease = {
                            fillViewModel.onQuantityChange(cartItem.product.id, cartItem.quantity + 1)
                        },
                        onQuantityDecrease = {
                            fillViewModel.onQuantityChange(cartItem.product.id, cartItem.quantity - 1)
                        }
                    )
                }
            }
        }
    }
}