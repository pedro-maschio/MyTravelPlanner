package com.pedro.solutions.mytravelplanning.ui.screens.create

sealed class CreateTravelUiEvent {
    object OnTravelDeleted : CreateTravelUiEvent()
    object OnTravelCreated : CreateTravelUiEvent()
    data class OnDayAdded(val dayTitle: String) : CreateTravelUiEvent()
    object ShareTravel : CreateTravelUiEvent()
    data class SetDropdownMenuShowing(val isShowing: Boolean) : CreateTravelUiEvent()
    data class SetIsEditing(val isEditing: Boolean) : CreateTravelUiEvent()
    data class SetDatePickerShowing(val isShowing: Boolean) : CreateTravelUiEvent()
    data class SetDeleteDialogShowing(val isShowing: Boolean) : CreateTravelUiEvent()
    data class SetDetailsAlertDialogShowing(val isShowing: Boolean) : CreateTravelUiEvent()
    data class DeleteDay(val index: Int) : CreateTravelUiEvent()
    data class AddActivity(val index: Int) : CreateTravelUiEvent()
    data class UpdateTravelDayText(val index: Int, val newText: String) : CreateTravelUiEvent()
    data class UpdateTravelName(val newText: String) : CreateTravelUiEvent()
    data class UpdateTravelActivityText(
        val dayIndex: Int,
        val activityIndex: Int,
        val newText: String
    ) : CreateTravelUiEvent()

    data class DateRangeSelected(val range: Pair<Long?, Long?>) : CreateTravelUiEvent()
}