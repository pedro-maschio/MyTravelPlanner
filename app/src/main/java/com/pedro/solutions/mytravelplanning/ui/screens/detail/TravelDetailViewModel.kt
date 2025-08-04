package com.pedro.solutions.mytravelplanning.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.navigation.TravelRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class TravelDetailViewModel(val repository: TravelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TravelDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadTravelDetail(travelData: TravelRoutes.TravelDetailScreen) = viewModelScope.launch {
        val travelGuide = travelData.travelGuideJson
        val travelItems = mutableListOf<TravelType>()
        try {
            travelGuide?.let {
                Json.decodeFromString<TravelGuide>(travelGuide).days.forEachIndexed { index, item ->
                    travelItems.add(
                        TravelType.Day(
                            index = index, title = item?.title.orEmpty()
                        )
                    )
                    item?.activities?.forEachIndexed { activityIndex, activity ->
                        travelItems.add(
                            TravelType.Activity(
                                index = activityIndex, dayIndex = index, title = activity
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // TODO: Log this, this should never happen
        }

        if (travelGuide == null && travelData.travelId != null) {
            val travelGuideWithDays = repository.loadTravelDetail(travelData.travelId)
            travelGuideWithDays.days.forEachIndexed {  index, item ->
                travelItems.add(
                    TravelType.Day(
                        index = index, title = item.day.title.orEmpty()
                    )
                )
                item.activities.forEachIndexed { activityIndex, activity ->
                    travelItems.add(
                        TravelType.Activity(
                            index = activityIndex, dayIndex = index, title = activity.title
                        )
                    )
                }
            }
        }

        _uiState.update { it.copy(travelItems = travelItems) }
    }
}
