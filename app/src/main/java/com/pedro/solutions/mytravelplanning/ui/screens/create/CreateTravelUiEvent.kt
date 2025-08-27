package com.pedro.solutions.mytravelplanning.ui.screens.create

sealed class CreateTravelUiEvent {
    object OnTravelDeleted : CreateTravelUiEvent()
    object OnTravelCreated : CreateTravelUiEvent()
    object OnDayAdded : CreateTravelUiEvent()
}