package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.ConductorViewModel
import com.chaikasoft.app.ui.viewModels.FillViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.ui.components.product.CartProductItem
import com.chaikasoft.app.ui.mappers.toUiModel
import com.chaikasoft.app.ui.theme.ProductDimens

@Composable
fun TemplateConfirmView(
    fillViewModel: FillViewModel,
    conductorViewModel: ConductorViewModel,
    navController: NavController,
) {
    val cartItems by fillViewModel.items.collectAsStateWithLifecycle()
    val conductor by conductorViewModel.conductor.collectAsStateWithLifecycle()

    val productsInPackage = remember(cartItems) {
        cartItems
            .filter { it.quantity > 0 }
            .map { it.product }
    }

    Scaffold(
        bottomBar = {
            TemplateConfirmBottomBar(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = {
                    val conductorId = conductor?.id ?: return@TemplateConfirmBottomBar
                    fillViewModel.onAddOperation(conductorId)

                    navController.navigate(Routes.PRODUCT_PACKAGE) {
                        popUpTo(Routes.TEMPLATE_EDIT) { inclusive = true }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (productsInPackage.isEmpty()) {
                EmptyPackageState(
                    modifier = Modifier.weight(1f)
                )
            } else {
                FinalPackageList(
                    cartItems = cartItems,
                    fillViewModel = fillViewModel
                )
            }
        }
    }
}

@Composable
fun FinalPackageList(
    cartItems: List<CartItemDomain>,
    fillViewModel: FillViewModel
) {
    LazyVerticalGrid (
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(ProductDimens.CartPadding)
    ) {
        items(cartItems, key = { it.product.id }) { product ->
            CartProductItem(
                product = product.toUiModel(),
                onAddToCart = { },
                onQuantityIncrease = { fillViewModel.onQuantityChange(product.product.id, product.quantity+1) },
                onQuantityDecrease = { fillViewModel.onQuantityChange(product.product.id, product.quantity-1) },
                onRemove = { fillViewModel.onRemove(product.product.id) }
            )
        }
    }
}


@Composable
private fun EmptyPackageState(
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Пакет пуст",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TemplateConfirmBottomBar(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(
                    topStart = ProductDimens.CornerRadiusL,
                    topEnd = ProductDimens.CornerRadiusL
                )
            ),
        shape = RoundedCornerShape(
            topStart = ProductDimens.CornerRadiusL,
            topEnd = ProductDimens.CornerRadiusL
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProductDimens.PaddingL),
            horizontalArrangement = Arrangement.spacedBy(ProductDimens.PaddingM)
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(ProductDimens.ButtonHeightL),
                onClick = onBackClick,
                shape = RoundedCornerShape(ProductDimens.CornerRadiusM),
            ) {
                Text("НАЗАД")
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(ProductDimens.ButtonHeightL),
                onClick = onConfirmClick,
                shape = RoundedCornerShape(ProductDimens.CornerRadiusM),
            ) {
                Text("ПОДТВЕРДИТЬ")
            }
        }
    }
}
