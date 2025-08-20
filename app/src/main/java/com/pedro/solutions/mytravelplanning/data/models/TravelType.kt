package com.pedro.solutions.mytravelplanning.data.models

sealed class TravelType(val index: Int) {
    class Day(index: Int, val title: String) : TravelType(index)
    class Activity(index: Int, val dayIndex: Int, val title: String) :
        TravelType(index)
}