package com.example.chaika.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                navController.navigate(Routes.TEMPLATE_SEARCH) {
                    popUpTo(Routes.PRODUCT_ENTRY) { inclusive = false }
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .height(46.dp)
                .width(280.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                fontSize = 18.sp,
                text = "ЗАПОЛНИТЬ ПАКЕТ"
            )
        }
    }
}