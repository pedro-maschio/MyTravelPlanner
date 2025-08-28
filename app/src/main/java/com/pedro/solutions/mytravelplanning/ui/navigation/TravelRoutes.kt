package com.pedro.solutions.mytravelplanning.ui.navigation


import kotlinx.serialization.Serializable

@Serializable
sealed class TravelRoutes {
    @Serializable
    data class CreateTravelScreen(val travelId: Long? = null) : TravelRoutes()

    @Serializable
    data object MainScreen : TravelRoutes()

    @Serializable
    data class TravelDetailScreen(val travelId: Long? = null, val travelGuideJson: String? = null) :
        TravelRoutes()
}