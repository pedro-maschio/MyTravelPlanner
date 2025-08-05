package com.pedro.solutions.mytravelplanning.data.models.openai


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.O)
data class TravelGuide(
    val createdAt: Long = -1,
    val updatedAt: Long = -1,
    val travelDate: Instant = Instant.ofEpochMilli(0L),
    val travelName: String = "",
    val days: List<Day?> = emptyList()
)