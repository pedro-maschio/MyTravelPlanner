package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.Day
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CreateTravelViewModel(val repository: TravelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTravelUiState())
    val uiState = _uiState.asStateFlow()
    var internalTravelId: Long? = null

    fun resetUiState() {
        internalTravelId = null
        _uiState.value = CreateTravelUiState()
    }

    fun loadTravel(travelId: Long?) = viewModelScope.launch {
        if (travelId == null) return@launch

        showLoading()
        internalTravelId = travelId
        val travelGuideWithDays = repository.loadTravelDetail(travelId)
        var travelGuide = TravelGuide()

        travelGuide = travelGuide.copy(
            travelName = travelGuideWithDays.travelGuide.travelName,
            days = travelGuideWithDays.days.map { dayWithActivities ->
                Day(
                    title = dayWithActivities.day.title,
                    activities = dayWithActivities.activities.map { it.title })
            },
            formattedStartDate = travelGuideWithDays.travelGuide.formattedStartDate,
            formattedEndDate = travelGuideWithDays.travelGuide.formattedEndDate,
            createdAt = travelGuideWithDays.travelGuide.createdAt,
            updatedAt = travelGuideWithDays.travelGuide.updatedAt
        )
        _uiState.update {
            it.copy(
                formattedCreatedAt = getTravelDateFormatted(
                    dateInMillis = travelGuideWithDays.travelGuide.createdAt, includeTime = true
                ),
                formattedUpdatedAt = getTravelDateFormatted(
                    dateInMillis = travelGuideWithDays.travelGuide.updatedAt, includeTime = true
                ),
                travel = travelGuide,
                hasTravelDates = travelGuideWithDays.travelGuide.formattedStartDate.isNotEmpty() && travelGuideWithDays.travelGuide.formattedEndDate.isNotEmpty()
            )
        }
        buildTravelState()
        hideLoading()
    }

    fun showLoading() {
        _uiState.update { it.copy(isLoading = true) }
    }


    fun hideLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }


    fun buildTravelState() {
        val currentTravel = _uiState.value.travel
        val newTravels = buildList {
            currentTravel.days.forEachIndexed { index, day ->
                if (day != null) {
                    add(
                        TravelType.Day(
                            index = index, title = day.title.orEmpty()
                        )
                    )
                    day.activities.forEachIndexed { activityIndex, activity ->
                        add(
                            TravelType.Activity(
                                index = activityIndex, dayIndex = index, title = activity
                            )
                        )
                    }
                }
            }
        }

        _uiState.update {
            it.copy(
                travelName = _uiState.value.travel.travelName, travels = newTravels
            )
        }
    }

    fun updateTravelName(travelName: String) {
        _uiState.update {
            it.copy(
                travelName = travelName, travel = it.travel.copy(travelName = travelName)
            )
        }
    }

    fun setEditingState(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }
    }

    fun onDayAdded(dayTitle: String) {
        _uiState.update {
            it.copy(
                travel = it.travel.copy(
                    days = _uiState.value.travel.days.plus(
                        Day(
                            title = "$dayTitle ${_uiState.value.travel.days.size + 1}",
                            activities = emptyList()
                        )
                    )
                )
            )
        }
        buildTravelState()
    }

    fun addActivity(index: Int) {
        val activities = _uiState.value.travel.days.getOrNull(index)?.activities
        _uiState.value.travel.days.getOrNull(index)?.activities =
            activities?.toMutableList()?.plus("") ?: listOf("")
        buildTravelState()
    }

    fun updateTravelDayText(index: Int, newText: String) {
        _uiState.update { state ->
            state.copy(
                travel = state.travel.copy(
                    days = state.travel.days.mapIndexed { dIndex, day ->
                        if (dIndex == index && day != null) day.copy(title = newText)
                        else day
                    })
            )
        }

        buildTravelState()
    }

    fun updateTravelActivityText(dayIndex: Int, activityIndex: Int, newText: String) {
        _uiState.update { state ->
            state.copy(
                travel = state.travel.copy(
                    days = state.travel.days.mapIndexed { index, day ->
                        if (index == dayIndex && day != null) {
                            day.copy(
                                activities = day.activities.mapIndexed { aIndex, activity ->
                                    if (aIndex == activityIndex) newText else activity
                                })
                        } else day
                    })
            )
        }

        buildTravelState()
    }

    fun setDropdownMenuShowing(isShowing: Boolean) {
        _uiState.update { it.copy(isDropDownMenuShowing = isShowing) }
    }

    fun setDetailsAlertDialogShowing(isShowing: Boolean) {
        _uiState.update { it.copy(isDetailsAlertDialogShowing = isShowing) }
    }

    fun setDeleteDialogShowing(isShowing: Boolean) {
        _uiState.update { it.copy(isDeleteDialogShowing = isShowing) }
    }

    fun setDatePickerModal(isShowing: Boolean) {
        _uiState.update { it.copy(isDatePickerShowing = isShowing) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTravelDateFormatted(
        dateInMillis: Long,
        includeTime: Boolean = false,
        fixTimeZone: Boolean = false
    ): String {
        val datePattern = if (includeTime) "dd/MM/yyyy HH:mm:ss" else "dd/MM/yyyy"
        val startInstant = Instant.ofEpochMilli(dateInMillis)
        val localStartDate = if (fixTimeZone) startInstant.atZone(ZoneOffset.UTC)
            .toLocalDateTime() else startInstant.atZone(ZoneOffset.systemDefault())
            .toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern(datePattern) //  HH:mm:ss

        val formattedStartDate = localStartDate.format(formatter)
        return formattedStartDate
    }

    fun onDateRangeSelected(dateRange: Pair<Long?, Long?>) {
        if (dateRange.first == null || dateRange.second == null) return

        val formattedStartDate =
            getTravelDateFormatted(dateInMillis = dateRange.first!!, fixTimeZone = true)
        val formattedEndDate =
            getTravelDateFormatted(dateInMillis = dateRange.second!!, fixTimeZone = true)
        internalTravelId?.let {
            // If the internalTravelId is null, it means we are dealing with a new travel (that will be
            // later created with the correct formattedStartDate/formattedEndDate from the uiState
            viewModelScope.launch {
                repository.addTravelDate(
                    travelId = it,
                    formattedStartDate = formattedStartDate,
                    formattedEndDate = formattedEndDate
                )
            }
        }
        _uiState.update {
            it.copy(
                hasTravelDates = true, travel = it.travel.copy(
                    formattedStartDate = formattedStartDate, formattedEndDate = formattedEndDate
                )
            )
        }
    }

    fun createTravel() = viewModelScope.launch {
        if (internalTravelId != null) { // User is editing an existing trip
            repository.updateTravelGuide(
                travelId = internalTravelId!!,
                travelGuide = _uiState.value.travel.copy(updatedAt = System.currentTimeMillis())
            )
        } else {
            repository.insertTravelGuide(
                _uiState.value.travel.copy(
                    travelName = _uiState.value.travelName,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun shareTravel() {
        setDropdownMenuShowing(false)
        val travelText = buildString {
            if (uiState.value.travel.travelName.isNotEmpty()) {
                appendLine(_uiState.value.travel.travelName)
            }
            if (uiState.value.hasTravelDates) {
                appendLine("${_uiState.value.travel.formattedStartDate} - ${_uiState.value.travel.formattedEndDate}")
            }
            _uiState.value.travel.days.forEach {
                if (it != null) {
                    appendLine("  - ${it.title}")
                    it.activities.forEach { activity ->
                        appendLine("    - $activity")
                    }
                }
            }
        }
        _uiState.update { it.copy(shareTravelText = travelText) }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogShowing = true) }
    }

    fun hideDeleteDialog() {
        setDropdownMenuShowing(false)
        _uiState.update { it.copy(isDeleteDialogShowing = false) }
    }

    fun setIsEditing(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }

        if (!isEditing) {
            updateDaysIndexes()
            buildTravelState()
        }
    }

    private fun updateDaysIndexes() {
        val travels = uiState.value.travels.toMutableList()

        var lastDayIndex = 0
        val updatedTravels: List<TravelType> = travels.mapIndexed { index, travelType ->
            if (travelType is TravelType.Activity) {
                TravelType.Activity(
                    index = lastDayIndex - index - 1,
                    dayIndex = lastDayIndex,
                    title = travelType.title
                )
            } else {
                lastDayIndex++
                TravelType.Day(
                    index = (travelType as TravelType.Day).index, title = travelType.title
                )
            }
        }
        _uiState.update { it.copy(travels = updatedTravels.toList()) }
    }

    fun deleteDay(index: Int) {
        _uiState.update { state ->
            state.copy(
                travel = state.travel.copy(
                    days = state.travel.days.filterIndexed { dIndex, _ ->
                        dIndex != index
                    })
            )
        }
        buildTravelState()
    }

    fun deleteTravel() = viewModelScope.launch {
        hideDeleteDialog()
        if (internalTravelId != null) {
            repository.deleteTravel(internalTravelId!!)
        }
    }
}