package com.pedro.solutions.mytravelplanning.data.models

import java.util.Date

data class Travel(
    val startingPoint: String? = null,
    val endingPoint: String? = null,
    val details: String? = null,
    val durationInDays: String = ""
)