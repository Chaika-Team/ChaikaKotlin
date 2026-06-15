package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.ui.components.template.CheckDialog
import com.chaikasoft.app.ui.navigation.Routes
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = productViewModel::onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                placeholder = { Text("Поиск по названию") },
                singleLine = true,
                shape = RoundedCornerShape(ProductDimens.CornerRadiusM)
            )

            ProductScreenContent(
                pagingItems = pagingItems,
                cartItems = cartItems,
                modifier = Modifier.weight(1f),
                onAddToCart = fillViewModel::onAdd,
                onQuantityChange = fillViewModel::onQuantityChange,
                onRemove = fillViewModel::onRemove,
                onNextClick = {
                    val conductorId = conductorViewModel.conductor.value?.id
                    if (conductorId == null) {
                        showNoConductorError = true
                    } else {
                        navController.navigate(Routes.TEMPLATE_CONFIRM)
                    }
                }
            )
        }

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
