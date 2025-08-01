package com.pedro.solutions.mytravelplanning.data.models

sealed class TravelType {
    class Day(val index: Int, val title: String) : TravelType()
    class Activity(val title: String) : TravelType()
}