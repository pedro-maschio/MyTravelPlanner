package com.pedro.solutions.mytravelplanning.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.Day
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTravelViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTravelUiState())
    val uiState = _uiState.asStateFlow()

    fun buildTravelState() {
        val currentTravel = _uiState.value.travel
        val newTravels = buildList {
            currentTravel.days.forEachIndexed { index, day ->
                if (day != null) {
                    add(TravelType.Day(index = index, title = day.title.orEmpty()))
                    day.activities.forEachIndexed { activityIndex, activity ->
                        add(
                            TravelType.Activity(
                                index = activityIndex,
                                dayIndex = index,
                                title = activity
                            )
                        )
                    }
                }
            }
        }

        _uiState.update { it.copy(travels = newTravels) }
    }


    fun addDefaultDay() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                travel = it.travel.copy(
                    days = listOf(
                        Day(
                            title = "Day 1",
                            activities = emptyList()
                        )
                    )
                )
            )
        }
        buildTravelState()
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
                    }
                )
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
                                }
                            )
                        } else day
                    }
                )
            )
        }

        buildTravelState()
    }

    fun deleteDayOrActivity(index: Int) {

    }
}