package com.pedro.solutions.mytravelplanning.data.models.openai

import kotlinx.serialization.Serializable

@Serializable
data class TravelGuide(
    val days: List<Day?>?
)