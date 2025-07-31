package com.pedro.solutions.mytravelplanning.ui.screens.detail


sealed class TravelItem {
    class Day(val index: Int, val title: String) : TravelItem()
    class Activity(val title: String) : TravelItem()
}

data class TravelDetailUiState(val travelItems: List<TravelItem> = emptyList(), val isEditing: Boolean = false)