package com.pedro.solutions.mytravelplanning.ui.screens.main

sealed class MainScreenUiEvent {
    class OpenCreateTravelScreen(val travelId: Long): MainScreenUiEvent()
    object GoBack : MainScreenUiEvent()
}