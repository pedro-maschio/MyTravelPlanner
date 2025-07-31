package com.pedro.solutions.mytravelplanning.ui.navigation

import kotlinx.serialization.Serializable


object TravelsRoutes {
    @Serializable
    data object IntroScreen

    @Serializable
    data class GenerateTravelScreen(val id: Int? = null)

    @Serializable
    data object MainScreen

    @Serializable
    data class TravelDetailScreen(val travelGuideJson: String)
}