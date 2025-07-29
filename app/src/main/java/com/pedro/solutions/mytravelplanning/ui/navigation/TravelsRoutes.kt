package com.pedro.solutions.mytravelplanning.ui.navigation

import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import kotlinx.serialization.Serializable


object TravelsRoutes {
    @Serializable
    data object IntroScreen

    @Serializable
    data class CreateTravelScreen(val id: Int? = null)

    @Serializable
    data object MainScreen

    @Serializable
    data class TravelDetailScreen(val travelGuideJson: String)
}