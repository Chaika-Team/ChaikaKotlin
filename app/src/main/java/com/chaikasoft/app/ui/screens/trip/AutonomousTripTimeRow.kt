package com.chaikasoft.app.ui.screens.trip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
internal fun TimeRow(
    arrival: LocalDateTime?,
    onArrival: (LocalDateTime?) -> Unit,
    departure: LocalDateTime?,
    onDeparture: (LocalDateTime?) -> Unit
) {
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Pill(
            text = departure.formatOr("Сегодня, 22:00"),
            modifier = Modifier.weight(1f),
            onClick = {
                showDateTimePicker(
                    context = context,
                    initial = departure ?: LocalDateTime.now()
                ) { picked -> onDeparture(picked) }
            }
        )
        Pill(
            text = arrival.formatOr("Завтра, 21:32"),
            modifier = Modifier.weight(1f),
            onClick = {
                showDateTimePicker(
                    context = context,
                    initial = arrival ?: LocalDateTime.now()
                ) { picked -> onArrival(picked) }
            }
        )
    }
}

@Composable
private fun Pill(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                text,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun showDateTimePicker(
    context: Context,
    initial: LocalDateTime = LocalDateTime.now(),
    onPicked: (LocalDateTime) -> Unit
) {
    val initDate = initial.toLocalDate()
    val initTime = initial.toLocalTime()

    val dateDialog = DatePickerDialog(
        context,
        { _, year, month0, day ->
            val date = LocalDate.of(year, month0 + 1, day)

            val is24 = DateFormat.is24HourFormat(context)
            val timeDialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    val time = LocalTime.of(hour, minute)
                    onPicked(LocalDateTime.of(date, time))
                },
                initTime.hour,
                initTime.minute,
                is24
            )
            timeDialog.show()
        },
        initDate.year,
        initDate.monthValue - 1,
        initDate.dayOfMonth
    )

    dateDialog.show()
}

private fun LocalDateTime?.formatOr(fallback: String): String {
    if (this == null) return fallback
    return this.format(DateTimeFormatter.ofPattern("d MMM, HH:mm"))
}
