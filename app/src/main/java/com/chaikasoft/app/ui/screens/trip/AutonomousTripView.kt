package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewmodels.AutonomousViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AutonomousTripScreen(viewModel: AutonomousViewModel, navController: NavController) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fromSuggestions = viewModel.fromSuggestions.collectAsLazyPagingItems()
    val toSuggestions = viewModel.toSuggestions.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is AutonomousViewModel.Event.ShiftStarted -> {
                    navController.navigate(Routes.TRIP_MAIN) {
                        popUpTo(Routes.TRIP_GRAPH) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }

                is AutonomousViewModel.Event.Info -> snackbarHostState.showSnackbar(ev.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ButtonSurface(
                    buttonText = if (state.isSubmitting) "СОХРАНЯЕМ..." else "ЗАВЕРШИТЬ",
                    onClick = {
                        viewModel.submit()
                        viewModel.clearState()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { inner ->
        AutonomousTripContent(
            state = state,
            fromSuggestions = fromSuggestions,
            toSuggestions = toSuggestions,
            onTrainNumberChange = viewModel::onTrainNumberChange,
            onFromQueryChange = viewModel::onFromQueryChange,
            onSelectFrom = viewModel::onSelectFrom,
            onToQueryChange = viewModel::onToQueryChange,
            onSelectTo = viewModel::onSelectTo,
            onDepartureChange = viewModel::onDepartureChange,
            onArrivalChange = viewModel::onArrivalChange,
            onCarriageNumberChange = viewModel::onCarriageNumberChange,
            onCarriageClassTypeChange = viewModel::onCarriageClassTypeChange,
            modifier = Modifier.padding(inner)
        )
    }
}
