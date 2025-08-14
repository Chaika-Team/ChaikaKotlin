package com.example.chaika.ui.screens.product.views

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.ui.components.product.CartProductItem
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ProductDimens
import com.example.chaika.ui.viewModels.TemplateViewModel

@Composable
fun TemplateEditView(
    templateId: Int,
    viewModel: TemplateViewModel,
    navController: NavController,
) {
    val templateState = produceState<TemplateDomain?>(initialValue = null, templateId) {
        value = viewModel.getTemplateDetail(templateId)
        Log.i("TemplateDetailView", "Got value: $value")
    }
    val template = templateState.value

    Box(modifier = Modifier.fillMaxSize()) {
        if (template == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Загрузка...")
            }
            return@Box
        }

        // Если контент пуст, показываем заглушку
        if (template.content.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Список пуст")
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 72.dp), // Оставляем место для кнопки
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(ProductDimens.CartPadding)
            ) {
                items(template.content, key = { it.productId }) { product ->
                    val id = product.productId.toString()
                    CartProductItem(
                        product = Product(
                            id = product.productId,
                            name = id,
                            description = template.description,
                            image = "",
                            price = 100.0,
                            isInCart = true,
                            quantity = 3,
                            isInPackage = false,
                            packageQuantity = 0
                        ),
                        onAddToCart = { },
                        onQuantityIncrease = { },
                        onQuantityDecrease = { },
                        onRemove = { }
                    )
                }
            }
        }

        // Кнопка фиксируется внизу экрана
        ButtonSurface(
            buttonText = "ДАЛЕЕ",
            onClick = {
                navController.navigate(Routes.TEMPLATE_CHECK)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp) // Добавляем отступы для безопасности
        )
    }
}
