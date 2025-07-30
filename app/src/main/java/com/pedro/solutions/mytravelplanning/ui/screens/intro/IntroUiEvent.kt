package com.pedro.solutions.mytravelplanning.ui.screens.intro

sealed class IntroUiEvent {
    data object SaveSelectedVehicle : IntroUiEvent()
}