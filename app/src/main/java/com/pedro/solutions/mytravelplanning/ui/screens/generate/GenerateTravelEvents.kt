package com.pedro.solutions.mytravelplanning.ui.screens.generate

import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

sealed class GenerateTravelEvents {
    class GoToListing(val travelGuide: TravelGuide): GenerateTravelEvents()
}