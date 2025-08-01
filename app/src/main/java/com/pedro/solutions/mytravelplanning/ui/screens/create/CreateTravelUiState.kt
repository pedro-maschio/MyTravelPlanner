package com.pedro.solutions.mytravelplanning.ui.screens.create

import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide


sealed class TravelType {
    class Day(val title: String) : TravelType()
    class Activity(val title: String) : TravelType()
}
data class CreateTravelUiState(val travels: List<TravelType> = emptyList(), val travel: TravelGuide = TravelGuide())