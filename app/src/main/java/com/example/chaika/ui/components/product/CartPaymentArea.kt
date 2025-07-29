package com.example.chaika.ui.components.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.ui.components.trip.dashedBorder
import com.example.chaika.ui.theme.ChaikaTheme
import com.example.chaika.ui.theme.ProductDimens
import androidx.compose.ui.res.stringResource
import com.example.chaika.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPaymentArea(
    totalCost: Double,
    conductors: List<ConductorDomain>,
    selectedConductor: ConductorDomain?,
    onConductorSelected: (ConductorDomain) -> Unit,
    onPayCash: () -> Unit,
    onPayCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(cornerRadius = ProductDimens.CornerRadiusL),
        shape = RoundedCornerShape(
            topStart = ProductDimens.CornerRadiusL,
            topEnd = ProductDimens.CornerRadiusL
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProductDimens.PaddingL),
            verticalArrangement = Arrangement.spacedBy(ProductDimens.PaddingM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.cart_payment_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = ProductDimens.TitleFontSizeL
                )

                Text(
                    text = "%.2f ₽".format(totalCost),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = ProductDimens.TitleFontSizeL
                )
            }

            Text(
                text = stringResource(id = R.string.cart_payment_conductor),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = ProductDimens.LabelFontSizeM
            )
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedConductor?.let { "${it.familyName} ${it.name}" } ?: stringResource(id = R.string.cart_payment_select_conductor),
                    textStyle = TextStyle(fontSize = ProductDimens.LabelFontSizeM),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .height(ProductDimens.FieldHeight)
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(ProductDimens.CornerRadiusM),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    conductors.forEach { conductor ->
                        DropdownMenuItem(
                            text = { Text("${conductor.familyName} ${conductor.name}") },
                            onClick = {
                                onConductorSelected(conductor)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ProductDimens.PaddingM)
            ) {
                Button(
                    onClick = onPayCash,
                    modifier = Modifier.height(ProductDimens.ButtonHeightL).weight(1f),
                    shape = RoundedCornerShape(ProductDimens.CornerRadiusM)
                ) {
                    Text(
                        text = stringResource(id = R.string.cart_payment_cash),
                        fontSize = ProductDimens.LabelFontSizeM
                    )
                }
                Button(
                    onClick = onPayCard,
                    modifier = Modifier.height(ProductDimens.ButtonHeightL).weight(1f),
                    shape = RoundedCornerShape(ProductDimens.CornerRadiusM)
                ) {
                    Text(
                        text = stringResource(id = R.string.cart_payment_card),
                        fontSize = ProductDimens.LabelFontSizeM
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPaymentAreaPreview() {
    val conductors = listOf(
        ConductorDomain(1, "Иван", "Иванов", "Иванович", "EMP001", ""),
        ConductorDomain(2, "Анна", "Петрова", "Сергеевна", "EMP002", "")
    )
    var selected by remember { mutableStateOf<ConductorDomain?>(conductors.first()) }
    ChaikaTheme {
        CartPaymentArea(
            totalCost = 240.0,
            conductors = conductors,
            selectedConductor = selected,
            onConductorSelected = { selected = it },
            onPayCash = {},
            onPayCard = {}
        )
    }


} 