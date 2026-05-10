package com.chaikasoft.app.ui.components.product

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.chaikasoft.app.R

@Composable
fun ProductImage(imageUrl: String, modifier: Modifier = Modifier, contentDescription: String) {
    val fallbackPainter = painterResource(id = R.drawable.placeholder_chaika)

    AsyncImage(
        model = imageUrl.takeIf { it.isNotBlank() },
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(MaterialTheme.shapes.large),
        contentDescription = contentDescription
    )
}
