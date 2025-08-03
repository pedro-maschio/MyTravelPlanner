package com.pedro.solutions.mytravelplanning.ui.screens.main

sealed class MainScreenUiEvent {
    class OpenTravelDetail(val travelId: Long): MainScreenUiEvent()
}