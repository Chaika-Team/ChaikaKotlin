package com.chaikasoft.app.ui.components.template

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chaikasoft.app.domain.models.TemplateContentDomain

@Composable
fun ColumnOfContent(
    modifier: Modifier = Modifier,
    content: List<TemplateContentDomain>,
    maxVisible: Int = 4
) {
    Column(modifier = modifier) {
        content.take(maxVisible).forEach { item ->
            Text(
                text = "x${item.quantity} ProductId: ${item.productId}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (content.size > maxVisible) {
            Text(
                text = "Подробнее...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}