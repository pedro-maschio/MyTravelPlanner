package com.pedro.solutions.mytravelplanning.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.Day
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTravelViewModel(val repository: TravelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTravelUiState())
    private val _uiEvent = MutableSharedFlow<CreateTravelUiEvent>()

    val uiState = _uiState.asStateFlow()
    val uiEvent = _uiEvent

    var internalTravelId: Long? = null

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
            })
        _uiState.update { it.copy(travel = travelGuide) }
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
                    add(TravelType.Day(index = index, title = day.title.orEmpty()))
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
                travelName = _uiState.value.travel.travelName,
                travels = newTravels
            )
        }
    }

    fun updateTravelName(travelName: String) {
        _uiState.update {
            it.copy(
                travelName = travelName,
                travel = it.travel.copy(travelName = travelName)
            )
        }
    }


    fun addDefaultDay() {
        _uiState.update {
            it.copy(
                travel = it.travel.copy(
                    days = listOf(
                        Day(
                            title = "Day 1", activities = emptyList()
                        )
                    )
                )
            )
        }
        buildTravelState()
    }

    fun setEditingState(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }
    }

    fun addDay() {
        _uiState.update {
            it.copy(
                travel = it.travel.copy(
                    days = _uiState.value.travel.days.plus(
                        Day(
                            title = "Day ${_uiState.value.travel.days.size + 1}",
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

    fun createTravel() = viewModelScope.launch {
        if (internalTravelId != null) { // User is editing an existing trip
            repository.updateTravelGuide(travelId = internalTravelId!!, _uiState.value.travel)
        } else {
            repository.insertTravelGuide(_uiState.value.travel.copy(travelName = _uiState.value.travelName))
        }
        _uiEvent.emit(CreateTravelUiEvent.OnTravelCreated)
    }

    fun deleteTravel() = viewModelScope.launch {
        if (internalTravelId == null) return@launch

        repository.deleteTravel(internalTravelId!!)
        _uiEvent.emit(CreateTravelUiEvent.OnTravelDeleted)
    }
}