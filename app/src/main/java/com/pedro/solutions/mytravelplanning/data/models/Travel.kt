package com.pedro.solutions.mytravelplanning.data.models

data class Travel(
    val startingPoint: String? = null,
    val endingPoint: String? = null,
    val details: String? = null,
    val durationInDays: String = ""
)