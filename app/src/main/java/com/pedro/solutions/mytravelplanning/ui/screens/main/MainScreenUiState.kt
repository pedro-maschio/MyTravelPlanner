package com.pedro.solutions.mytravelplanning.ui.screens.main


data class MainScreenTravel(
    val travelName: String,
    val travelId: Long,
    val isSelected: Boolean
)

data class MainScreenUiState(
    val travels: List<MainScreenTravel> = emptyList(),
    val shouldShowEmptyState: Boolean = false,
    val isLoading: Boolean = false,
    val isDropDownMenuShowing: Boolean = false,
    val isDeleteDialogShowing: Boolean = false,
    val isOnSelectionMode: Boolean = false,
    val selectedTravelIds: HashSet<Long> = hashSetOf()
)