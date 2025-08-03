package com.pedro.solutions.mytravelplanning.ui.screens.main


data class MainScreenTravel(
    val travelName: String,
    val travelId: Long
)
data class MainScreenUiState(val travels: List<MainScreenTravel> = emptyList())