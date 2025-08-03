package com.pedro.solutions.mytravelplanning.ui.navigation


import kotlinx.serialization.Serializable

@Serializable
sealed class TravelsRoutes {
    @Serializable
    data object IntroScreen: TravelsRoutes()

    @Serializable
    data class CreateTravelScreen(val travelId: Long? = null): TravelsRoutes()

    @Serializable
    data class GenerateTravelScreen(val id: Int? = null): TravelsRoutes()

    @Serializable
    data object MainScreen: TravelsRoutes()

    @Serializable
    data class TravelDetailScreen(val travelId: Long? = null, val travelGuideJson: String? = null): TravelsRoutes()
}