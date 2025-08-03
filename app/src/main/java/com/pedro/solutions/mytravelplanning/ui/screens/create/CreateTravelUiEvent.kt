package com.pedro.solutions.mytravelplanning.ui.screens.create

sealed class CreateTravelUiEvent {
    object OnTravelDeleted : CreateTravelUiEvent()
}