package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Единые «дефолты» для времени/длительности/стрелки.
 */
object TripTime {
    val DurationFontSize = 12.sp
    val DurationLineHeight = 12.sp
    val DurationArrowGap = 2.dp          // зазор между длительностью и стрелкой
    val ArrowHorizontalPadding = 8.dp    // сужаем линию/стрелку по ширине
    val ArrowBottomPadding = 16.dp
    val TimeFontSize = 20.sp
    val DayMonthFontSize = 14.sp
}

/**
 * Текст длительности c отключённым font padding и обрезкой в 1 строку.
 */
@Composable
fun DurationText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = LocalTextStyle.current.copy(
            fontSize = TripTime.DurationFontSize,
            lineHeight = TripTime.DurationLineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                trim = LineHeightStyle.Trim.Both,
                alignment = LineHeightStyle.Alignment.Proportional
            )
        ),
        modifier = modifier
    )
}

/** Удобная перегрузка — сразу час/минуты. */
@Composable
fun DurationText(
    hours: Int,
    minutes: Int,
    modifier: Modifier = Modifier
) = DurationText("$hours ч $minutes мин", modifier)

/**
 * Паддингованная «стрелка» (линия) — чтобы не дублировать .padding(...) вокруг Arrow().
 */
@Composable
fun arrowPadded(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = TripTime.ArrowHorizontalPadding,
    bottomPadding: Dp = TripTime.ArrowBottomPadding,
    content: @Composable () -> Unit = { Arrow() }
) {
    Box(
        modifier = modifier.padding(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = bottomPadding
        ),
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}

/** Крупное «hh:mm» — единый стиль и ограничения. */
@Composable
fun timeValueText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = TripTime.TimeFontSize,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/** Маленький текст даты (день/месяц). */
@Composable
fun dayMonthText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = TripTime.DayMonthFontSize,
        modifier = modifier
    )
}
