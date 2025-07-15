package com.pedro.solutions.mytravelplanning.ui.navigation

import kotlinx.serialization.Serializable


object TravelsRoutes {
    @Serializable
    data class CreateTravelScreen(val id: Int? = null)
}