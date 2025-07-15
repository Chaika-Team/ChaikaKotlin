package com.example.chaika.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ProductDimens {
    val ProductCardWidth = 165.dp
    val ProductCardHeight = 215.dp
    val PaddingM = 8.dp
    val PaddingL = 12.dp
    val ProductCornerRadius = 24.dp
    val BackGroundHeight = 130.dp
    val ImageWidth = 115.dp
    val ImageHeight = 122.dp
    val QuantitySelectorHeight = 30.dp
    val BackgroundSlantAngle = 1F
    val TextSizeSmall = 12.sp
    
    // Константы для CartProductItem
    object CartProductItem {
        val ImageSize = 78.dp
        val RemoveButtonPadding = 8.dp
        val QuantitySelectorWidth = 120.dp
        val QuantitySelectorHeight = 30.dp
        val NameFontSize = 17.sp
        val PriceFontSize = 20.sp
        val MaxNameLines = 2
    }

    // Константы для ArrowFAB
    object ArrowFAB {
        val Size = 56.dp
        val IconSize = 24.dp // предполагаемый размер, если понадобится
    }

    // Константы для CartFAB
    object CartFAB {
        val Width = 64.dp
        val Height = 88.dp
        val InnerWidth = 56.dp
        val InnerHeight = 80.dp
        val TopCornerRadius = 28.dp
        val BadgeSize = 20.dp
        val BadgeOverlap = (-5).dp
        val IconSize = 24.dp
        val BadgeCornerRadius = 20.dp
        val BadgeTextOffsetY = (-2).dp
        val ColumnPaddingHorizontal = 4.dp
        val SpacerHeight = 4.dp
        val PriceFontSize = 12.sp
        val BadgeFontSize = 12.sp
    }

    // Константы для ProductListView
    object ProductListView {
        val GridColumns = 2
        val GridContentPadding = 16.dp
        val FABCornerRadius = 30.dp
    }

    val CornerRadiusL = 24.dp // для CartPaymentArea и других крупных элементов
    val CornerRadiusM = 16.dp // для полей и кнопок
    val FieldHeight = 48.dp // для OutlinedTextField
    val ButtonHeightL = 64.dp // для крупных кнопок
    val TitleFontSizeL = 20.sp // для крупных заголовков
    val LabelFontSizeM = 14.sp // для подписей и кнопок
}