package com.pedro.solutions.mytravelplanning.ui.screens.create

import com.pedro.solutions.mytravelplanning.data.models.Travel

data class CreateTravelUiState(
    val travel: Travel = Travel(),
    var isLoading: Boolean = false,
    val showStartDateModal: Boolean = false,
    val showEndDateModal: Boolean = false,
    val showErrorScreen: Boolean = false
)
