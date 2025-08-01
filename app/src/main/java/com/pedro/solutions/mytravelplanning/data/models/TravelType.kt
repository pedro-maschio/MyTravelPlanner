package com.pedro.solutions.mytravelplanning.data.models

sealed class TravelType {
    class Day(val index: Int, val title: String) : TravelType()
    class Activity(val index: Int, val dayIndex: Int, val title: String) : TravelType()
}