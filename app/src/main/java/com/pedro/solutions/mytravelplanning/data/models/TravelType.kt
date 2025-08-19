package com.pedro.solutions.mytravelplanning.data.models

sealed class TravelType(val id: Long, val index: Int) {
    class Day(id: Long, index: Int, val title: String) : TravelType(id, index)
    class Activity(id: Long, index: Int, val dayIndex: Int, val title: String) :
        TravelType(id, index)
}