package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.FillViewModel
import com.chaikasoft.app.ui.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryView(
    navController: NavController,
    productViewModel: ProductViewModel,
    fillViewModel: FillViewModel
) {
    LaunchedEffect(Unit) {
        productViewModel.attachCart(fillViewModel.items)
    }

    ProductEntryContent(
        onFillPackageClick = {
            navController.navigate(Routes.TEMPLATE_SEARCH) {
                popUpTo(Routes.PRODUCT_ENTRY) { inclusive = false }
            }
        }
    )
}

@Composable
private fun ProductEntryContent(onFillPackageClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("productEntryScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onFillPackageClick,
            modifier = Modifier
                .testTag("productEntryFillPackageButton")
                .padding(16.dp)
                .widthIn(max = 280.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 46.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                fontSize = 18.sp,
                text = "ЗАПОЛНИТЬ ПАКЕТ"
            )
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun ProductEntryContentPreview() {
    ChaikaTheme {
        ProductEntryContent(onFillPackageClick = {})
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun ProductEntryContentWidePreview() {
    ChaikaTheme {
        ProductEntryContent(onFillPackageClick = {})
    }
}
