package com.pedro.solutions.mytravelplanning.ui.screens.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TravelDetailUiState())
    val uiState = _uiState.asStateFlow()
}