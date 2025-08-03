package com.pedro.solutions.mytravelplanning.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(val repository: TravelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    private val _uiEvent = MutableSharedFlow<MainScreenUiEvent>()
    val uiState = _uiState.asStateFlow()
    val uiEvent = _uiEvent

    fun loadTravels() = viewModelScope.launch {
        showLoading()
        _uiState.value = _uiState.value.copy(
            travels = repository.loadTravels()
                .map {
                    MainScreenTravel(
                        travelName = it.travelGuide.travelName,
                        travelId = it.travelGuide.id
                    )
                })
        hideLoading()
        updateEmptyState()
    }

    private fun updateEmptyState() =
        _uiState.update { it.copy(shouldShowEmptyState = _uiState.value.travels.isEmpty()) }

    private fun showLoading() = _uiState.update { it.copy(isLoading = true) }
    private fun hideLoading() = _uiState.update { it.copy(isLoading = false) }

    fun openTravelDetail(travelId: Long) = viewModelScope.launch {
        _uiEvent.emit(MainScreenUiEvent.OpenCreateTravelScreen(travelId))
    }
}