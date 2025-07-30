package com.pedro.solutions.mytravelplanning.ui.screens.intro

enum class SelectedOption {
    NONE,
    CAR,
    MOTORCYCLE
}

data class IntroUiState(
    val selectedOption: SelectedOption = SelectedOption.NONE,
    val isSaveButtonShowing: Boolean = false
)
