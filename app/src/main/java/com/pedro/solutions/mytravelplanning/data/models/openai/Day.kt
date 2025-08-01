package com.pedro.solutions.mytravelplanning.data.models.openai

import kotlinx.serialization.Serializable


@Serializable
data class Day(
    val title: String?,
    var activities: List<String> = emptyList()
)