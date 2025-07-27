package com.example.chaika.ui.components.template

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import com.example.chaika.ui.theme.ProductDimens

@Composable
fun ButtonSurface(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(
                    topStart = ProductDimens.CornerRadiusL,
                    topEnd = ProductDimens.CornerRadiusL
                )
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = ProductDimens.CornerRadiusL,
            topEnd = ProductDimens.CornerRadiusL
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProductDimens.PaddingL),
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.height(ProductDimens.ButtonHeightL).fillMaxWidth(),
                shape = RoundedCornerShape(ProductDimens.CornerRadiusM)
            ) {
                Text(
                    text = buttonText,
                    fontSize = ProductDimens.LabelFontSizeM
                )
            }
        }
    }
} 