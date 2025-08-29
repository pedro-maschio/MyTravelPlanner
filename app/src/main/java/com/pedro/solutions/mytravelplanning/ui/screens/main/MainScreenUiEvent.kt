package com.pedro.solutions.mytravelplanning.ui.screens.main

sealed class MainScreenUiEvent {
    class OpenCreateTravelScreen(val travelId: Long) : MainScreenUiEvent()
    object GoBack : MainScreenUiEvent()
    data class OnSelectionModeChanged(val isOnSelectionMode: Boolean) : MainScreenUiEvent()
    object HideDeleteDialog : MainScreenUiEvent()
    object ShowDeleteDialog : MainScreenUiEvent()
    data class SetSearchScreenExpanded(val expanded: Boolean) : MainScreenUiEvent()
    object ClearSearchQuery : MainScreenUiEvent()
    data class UpdateQuery(val query: String) : MainScreenUiEvent()
    data class OnSelectTravel(val travelId: Long) : MainScreenUiEvent()
    object OnDeleteTravelsClick : MainScreenUiEvent()
    data class SetDropDownMenuShowing(val isShowing: Boolean) : MainScreenUiEvent()
    object OnClickFloatingButton : MainScreenUiEvent()

}