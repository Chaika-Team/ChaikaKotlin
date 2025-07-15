package com.example.chaika.ui.screens.product.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryView(
    navController: NavController,
) {
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