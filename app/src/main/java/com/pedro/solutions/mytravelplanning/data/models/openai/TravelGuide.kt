package com.pedro.solutions.mytravelplanning.data.models.openai


import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.O)
data class TravelGuide(
    val createdAt: Long = -1,
    val updatedAt: Long = -1,
    val formattedStartDate: String = "",
    val formattedEndDate: String = "",
    val travelName: String = "",
    val days: List<Day?> = emptyList()
)