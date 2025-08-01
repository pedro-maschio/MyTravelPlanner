package com.pedro.solutions.mytravelplanning.ui.screens.detail

import com.pedro.solutions.mytravelplanning.data.models.TravelType

data class TravelDetailUiState(
    val travelItems: List<TravelType> = emptyList(),
    val isEditing: Boolean = false
)