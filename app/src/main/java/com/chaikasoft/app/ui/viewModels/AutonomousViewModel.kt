package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.helpers.OfflineTripBuildHelper
import com.chaikasoft.app.ui.helpers.OfflineTripBuildHelper.BuildError
import com.chaikasoft.app.ui.helpers.OfflineTripBuildHelper.BuildResult
import com.chaikasoft.app.ui.helpers.OfflineTripBuildHelper.Input
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AutonomousViewModel @Inject constructor(
    private val getPagedStationSuggestions: GetPagedStationSuggestionsUseCase,
    private val startShift: StartShiftUseCase,
) : ViewModel() {

    companion object {
        private const val SUGGEST_PAGE = 20
        private const val DEFAULT_CLASS_TYPE = "Не указан"
    }

    // -------- UI State --------

    data class UiState(
        val trainNumber: String = "",
        val fromQuery: String = "",
        val toQuery: String = "",
        val fromSelected: StationDomain? = null,
        val toSelected: StationDomain? = null,
        val departure: LocalDateTime? = null,
        val arrival: LocalDateTime? = null,
        val carriageNumber: String = "",
        val carriageClassType: String = "",
        val isSubmitting: Boolean = false,
        val buildErrors: Set<BuildError> = emptySet(),
        val lastMessage: String? = null
    ) {
        val isValid: Boolean
            get() = OfflineTripBuildHelper.isValid(
                Input(
                    trainNumber = trainNumber,
                    fromStation = fromSelected,
                    toStation = toSelected,
                    departure = departure,
                    arrival = arrival,
                    carriageNumber = carriageNumber,
                    carriageClassType = carriageClassType.ifBlank { null }
                )
            )
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _fromQuery = MutableStateFlow("")
    private val _toQuery = MutableStateFlow("")

    val fromSuggestions: Flow<PagingData<StationDomain>> =
        _fromQuery
            .map { it.trim() }
            .filter { it.length >= 2 }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query -> getPagedStationSuggestions(query, pageSize = SUGGEST_PAGE) }
            .cachedIn(viewModelScope)

    val toSuggestions: Flow<PagingData<StationDomain>> =
        _toQuery
            .map { it.trim() }
            .filter { it.length >= 2 }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query -> getPagedStationSuggestions(query, pageSize = SUGGEST_PAGE) }
            .cachedIn(viewModelScope)

    // -------- One-shot events --------

    sealed interface Event {
        data class ShiftStarted(val trip: TripDomain, val carriage: CarriageDomain) : Event
        data class Info(val message: String) : Event
    }

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    // -------- Inputs binding --------

    fun onTrainNumberChange(value: String) =
        update { copy(trainNumber = value).clearedErrorsFor(BuildError.TrainNumberEmpty) }

    fun onFromQueryChange(value: String) {
        _fromQuery.value = value               // ВАЖНО: запускаем поиск
        update {
            copy(fromQuery = value, fromSelected = null)
                .clearedErrorsFor(BuildError.FromStationMissing, BuildError.SameStations)
        }
    }

    fun onToQueryChange(value: String) {
        _toQuery.value = value                 // ВАЖНО: запускаем поиск
        update {
            copy(toQuery = value, toSelected = null)
                .clearedErrorsFor(BuildError.ToStationMissing, BuildError.SameStations)
        }
    }

    fun onSelectFrom(station: StationDomain) {
        _fromQuery.value = station.name        // синхронизируем с потоком подсказок
        update {
            copy(fromSelected = station, fromQuery = station.name)
                .clearedErrorsFor(BuildError.FromStationMissing, BuildError.SameStations)
        }
    }

    fun onSelectTo(station: StationDomain) {
        _toQuery.value = station.name          // синхронизируем с потоком подсказок
        update {
            copy(toSelected = station, toQuery = station.name)
                .clearedErrorsFor(BuildError.ToStationMissing, BuildError.SameStations)
        }
    }

    fun onDepartureChange(value: LocalDateTime?) =
        update { copy(departure = value).clearedErrorsFor(BuildError.DepartureMissing, BuildError.ArrivalNotAfterDeparture) }

    fun onArrivalChange(value: LocalDateTime?) =
        update { copy(arrival = value).clearedErrorsFor(BuildError.ArrivalMissing, BuildError.ArrivalNotAfterDeparture) }

    fun onCarriageNumberChange(value: String) =
        update { copy(carriageNumber = value).clearedErrorsFor(BuildError.CarriageNumberEmpty) }

    fun onCarriageClassTypeChange(value: String) =
        update { copy(carriageClassType = value) }

    fun submit(zone: ZoneId = ZoneId.systemDefault()) {
        val current = _state.value
        val input = Input(
            trainNumber = current.trainNumber,
            fromStation = current.fromSelected,
            toStation = current.toSelected,
            departure = current.departure,
            arrival = current.arrival,
            carriageNumber = current.carriageNumber,
            // пустой класс не валидируем — пускаем null, билдер сам подставит дефолт
            carriageClassType = current.carriageClassType.ifBlank { null }
        )

        viewModelScope.launch {
            update { copy(isSubmitting = true, buildErrors = emptySet(), lastMessage = null) }

            when (val result = OfflineTripBuildHelper.build(input, zone, DEFAULT_CLASS_TYPE)) {
                is BuildResult.Invalid -> {
                    // Если вдруг билдер вернул ошибки — просто покажем их в UI-состоянии
                    update { copy(isSubmitting = false, buildErrors = result.errors.toSet()) }
                }
                is BuildResult.Failure -> {
                    update { copy(isSubmitting = false, lastMessage = result.cause.message) }
                    _events.emit(Event.Info(result.cause.message ?: "Не удалось собрать поездку"))
                }
                is BuildResult.Success -> {
                    val (trip, carriage) = result.output
                    val started = runCatching { startShift(trip, carriage) }.getOrElse { false }
                    update { copy(isSubmitting = false) }
                    if (started) _events.emit(Event.ShiftStarted(trip, carriage))
                    else _events.emit(Event.Info("Уже есть активная смена"))
                }
            }
        }
    }

    fun clearState() {
        _fromQuery.value = ""
        _toQuery.value = ""

        _state.update { UiState() }
    }

    private inline fun update(reducer: UiState.() -> UiState) {
        _state.update(reducer)
    }

    private fun UiState.clearedErrorsFor(vararg toClear: BuildError): UiState {
        if (buildErrors.isEmpty()) return this
        val s = buildErrors - toClear.toSet()
        return if (s === buildErrors) this else copy(buildErrors = s)
    }
}
