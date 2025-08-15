package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyListItemInfo
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

@RequiresApi(Build.VERSION_CODES.O)
data class CreateTravelUiState(
    val travels: List<TravelType> = emptyList(),
    val travel: TravelGuide = TravelGuide(),
    val travelName: String = "",
    val isDeleteDialogShowing: Boolean = false,
    val isDropDownMenuShowing: Boolean = false,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val draggingItem: LazyListItemInfo? = null,
    val draggingIndex: Int? = null,
    val draggingAmount: Float = 0f
)