package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ProductViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryView(
    navController: NavController,
    viewModel: ProductViewModel
) {
    // TODO()
    val packageItems by viewModel.cartItems.collectAsState()

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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Create package")
        }
    }
}