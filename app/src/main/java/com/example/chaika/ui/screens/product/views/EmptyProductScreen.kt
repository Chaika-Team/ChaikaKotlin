package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ProductViewModel


@Composable
fun ProductEntryScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    // TODO()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate(Routes.PRODUCT_LIST) {
                    popUpTo(Routes.PRODUCT_ENTRY) { inclusive = false }
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .testTag("viewProductsButton")
        ) {
            Text(text = "View products")
        }
    }
}