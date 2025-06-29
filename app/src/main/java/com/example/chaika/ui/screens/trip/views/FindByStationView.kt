package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FindByStationView(
) {
    // TODO: Реализовать экран поиска по станции
        Column(
                modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                    ) {
                Text(
                        text = "FindByStation",
                        style = MaterialTheme.typography.displayLarge
                            )
            }
}