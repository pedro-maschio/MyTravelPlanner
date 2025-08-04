package com.pedro.solutions.mytravelplanning.ui.screens.create

import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

data class CreateTravelUiState(
    val travels: List<TravelType> = emptyList(),
    val travel: TravelGuide = TravelGuide(),
    val travelName: String = "",
    val isDropDownMenuShowing: Boolean = false,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false
)