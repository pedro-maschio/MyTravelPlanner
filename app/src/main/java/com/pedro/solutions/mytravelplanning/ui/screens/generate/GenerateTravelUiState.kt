package com.pedro.solutions.mytravelplanning.ui.screens.generate

import com.pedro.solutions.mytravelplanning.data.models.Travel

data class GenerateTravelUiState(
    val travel: Travel = Travel(),
    var isLoading: Boolean = false,
    val showStartDateModal: Boolean = false,
    val showEndDateModal: Boolean = false,
    val showErrorScreen: Boolean = false,
    val isDropDownMenuShowing: Boolean = false,
    val errorMessage: String = ""
)
