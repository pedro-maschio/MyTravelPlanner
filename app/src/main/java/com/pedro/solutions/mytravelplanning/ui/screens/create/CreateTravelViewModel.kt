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
        _uiState.update {
            it.copy(travels = it.travel.days.flatMapIndexed { index, day ->
                listOf(TravelType.Day(index = index, title = day?.title.orEmpty())) + day?.activities!!.map { activity ->
                    TravelType.Activity(activity)
                }
            })
        }
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
                            title = "Day ${_uiState.value.travel.days.size+1}",
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

    fun deleteDayOrActivity(index: Int) {

    }
}