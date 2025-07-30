package com.pedro.solutions.mytravelplanning.ui.screens.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.repository.TravelsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IntroViewModel(private val travelsRepository: TravelsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<IntroUiEvent?>()
    val uiEvents = _uiEvents

    fun onSelectedOptionChanged(selectedOption: SelectedOption) {
        val updatedSelectedOption = if (_uiState.value.selectedOption == selectedOption) {
            SelectedOption.NONE
        } else selectedOption
        val isSaveButtonShowing = updatedSelectedOption != SelectedOption.NONE
        _uiState.update {
            it.copy(
                selectedOption = updatedSelectedOption, isSaveButtonShowing = isSaveButtonShowing
            )
        }
    }

    fun onSaveSelectedVehicle() = viewModelScope.launch {
        val selectedOption = _uiState.value.selectedOption
        if(selectedOption == SelectedOption.NONE) {
            // TODO: Log this, this shouldn't happen
            return@launch
        }

        travelsRepository.saveSelectedVehicle(selectedOption)
        _uiEvents.emit(IntroUiEvent.SaveSelectedVehicle)
    }
}