package com.pedro.solutions.mytravelplanning.ui.screens.create

import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

sealed class CreateTravelEvents {
    class GoToListing(val travelGuide: TravelGuide): CreateTravelEvents()
}