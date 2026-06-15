package com.chaikasoft.app.ui.components.product

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.ui.theme.ProductDimens

@Composable
internal fun ProductItemQuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    QuantitySelector(
        quantity = quantity,
        onIncrease = onIncrease,
        onDecrease = onDecrease,
        modifier = modifier,
        colorBack = MaterialTheme.colorScheme.surfaceVariant,
        colorText = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
internal fun ProductItemAddButton(
    onClick: () -> Unit,
    @StringRes contentDescriptionRes: Int,
    modifier: Modifier = Modifier,
    iconScale: Float = 0.75F
) {
    Button(
        modifier = modifier,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = contentDescriptionRes),
            tint = Color.White,
            modifier = Modifier.fillMaxSize(iconScale)
        )
    }
}

@Composable
internal fun ProductItemRemoveButton(
    onClick: () -> Unit,
    @StringRes contentDescriptionRes: Int,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(id = contentDescriptionRes),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

internal fun Modifier.quantitySelectorSize(): Modifier = size(
    width = ProductDimens.CartProductItem.QuantitySelectorWidth,
    height = ProductDimens.CartProductItem.QuantitySelectorHeight
)
