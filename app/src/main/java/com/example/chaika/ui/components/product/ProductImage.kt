package com.example.chaika.ui.components.product

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ProductImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String
) {
    AsyncImage(
        model = imageUrl,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(MaterialTheme.shapes.large),
        contentDescription = contentDescription
    )
}