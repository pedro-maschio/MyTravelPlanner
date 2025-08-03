package com.pedro.solutions.mytravelplanning.data.models.openai

import kotlinx.serialization.Serializable

@Serializable
data class TravelGuide(
    val travelName: String = "",
    val days: List<Day?> = emptyList()
)