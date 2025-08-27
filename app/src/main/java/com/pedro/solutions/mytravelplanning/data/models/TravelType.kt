package com.pedro.solutions.mytravelplanning.data.models

sealed class TravelType(open val index: Int) {
    data class Day(override val index: Int, val title: String) : TravelType(index = index)
    data class Activity(override val index: Int, val dayIndex: Int, val title: String) :
        TravelType(index = index)
}