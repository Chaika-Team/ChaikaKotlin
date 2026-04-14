package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.components.product.CartPaymentArea
import com.chaikasoft.app.ui.components.product.CartProductItem
import com.chaikasoft.app.ui.components.product.SellResultBottomSheet
import com.chaikasoft.app.ui.mappers.toUiModel
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

    var selectedConductor by remember(conductors, currentConductor) {
        mutableStateOf(
            conductors.firstOrNull {
                it.id == currentConductor?.id
            } ?: conductors.firstOrNull()
        )
    }

    val totalCost = cartItems.sumOf { it.product.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(id = R.string.cart_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(ProductDimens.CartPadding)
                ) {
                    items(cartItems, key = { it.product.id }) { product ->
                        CartProductItem(
                            product = product.toUiModel(),
                            onAddToCart = { },
                            onQuantityIncrease = {
                                saleViewModel.onQuantityChange(
                                    product.product.id,
                                    product.quantity + 1
                                )
                            },
                            onQuantityDecrease = {
                                saleViewModel.onQuantityChange(
                                    product.product.id,
                                    product.quantity - 1
                                )
                            },
                            onRemove = { saleViewModel.onRemove(product.product.id) }
                        )
                    }
                }
            }
        }
        CartPaymentArea(
            totalCost = totalCost,
            conductors = conductors,
            selectedConductor = selectedConductor,
            onConductorSelected = { selectedConductor = it },
            onPayCash = {
                selectedConductor?.id?.let { saleViewModel.onSellCash(it) }
            },
            onPayCard = {
                selectedConductor?.id?.let { saleViewModel.onSellCard(it) }
            }
        )
    }
    SellResultBottomSheet(
        viewModel = saleViewModel,
        onClick = { navController.navigateUp() }
    )
}
