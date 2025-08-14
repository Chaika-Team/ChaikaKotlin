package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.R
import com.example.chaika.ui.components.template.CheckDialog
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.TemplateViewModel

@Composable
fun TemplateCheckView(
    viewModel: TemplateViewModel,
    navController: NavController,
) {
    val cartItems = viewModel.cartItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (cartItems.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.cart_empty), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid (
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(ProductDimens.CartPadding)
                ) {
                    items(cartItems.value, key = { it.id }) { product ->
                        ItemInfo(product)
                    }
                }
            }
        }
        CheckDialog(
            text = "Вы уверены?\nПожалуйста, проверьте содержимое пакета",
            onConfirm = {
                navController.navigate(Routes.PRODUCT_PACKAGE)
            },
            onDismiss = {
                navController.navigate(Routes.TEMPLATE_EDIT)
            }
        )
    }
}

@Composable
private fun ItemInfo(item: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ProductId: ${item.id}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "x${item.quantity}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
