package com.pedro.solutions.mytravelplanning.ui.navigation

import kotlinx.serialization.Serializable


object TravelsRoutes {
    @Serializable
    data object IntroScreen

    @Serializable
    data class CreateTravelScreen(val id: Int? = null)

    @Serializable
    data class MainScreen(val travelGuideJson: String)
}