package com.pedro.solutions.mytravelplanning.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(val repository: TravelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTravels()
    }

    fun loadTravels() = viewModelScope.launch {
        showLoading()
        _uiState.value = _uiState.value.copy(
            travels = repository.loadTravels()
                .map {
                    MainScreenTravel(
                        travelName = it.travelGuide.travelName,
                        travelId = it.travelGuide.id,
                        isSelected = false
                    )
                })
        updateSearchedTravels()
        hideLoading()
        updateEmptyState()
    }

    private fun updateEmptyState() =
        _uiState.update { it.copy(shouldShowEmptyState = _uiState.value.travels.isEmpty()) }

    private fun showLoading() = _uiState.update { it.copy(isLoading = true) }
    private fun hideLoading() = _uiState.update { it.copy(isLoading = false) }

    fun setOnSelectionMode(isOnSelectionMode: Boolean) {
        _uiState.update {
            it.copy(
                isOnSelectionMode = isOnSelectionMode,
                selectedTravelIds = hashSetOf(),
                travels = it.travels.map { travel -> travel.copy(isSelected = false) }
            )
        }
    }

    fun selectTravel(travelId: Long) {
        val isRemoving = travelId in _uiState.value.selectedTravelIds
        if (isRemoving) {
            _uiState.value.selectedTravelIds.remove(travelId)
        } else {
            _uiState.value.selectedTravelIds.add(travelId)
        }
        _uiState.update {
            it.copy(travels = it.travels.map { travel ->
                if (travel.travelId == travelId) travel.copy(
                    isSelected = !isRemoving
                ) else travel
            })
        }
        if (_uiState.value.selectedTravelIds.isEmpty()) {
            setOnSelectionMode(false)
        }
    }

    fun onDeleteTravelsClick() {
        val idsToDelete = uiState.value.selectedTravelIds.toList()
        setDropdownMenuShowing(false)
        viewModelScope.launch {
            idsToDelete.forEach {
                repository.deleteTravel(it)
            }
            loadTravels()
        }
        setOnSelectionMode(false)
        hideDeleteDialog()
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogShowing = false) }
        setDropdownMenuShowing(false)
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogShowing = true) }
    }

    fun setDropdownMenuShowing(isShowing: Boolean) {
        _uiState.update { it.copy(isDropDownMenuShowing = isShowing) }
    }

    fun setSearchScreenExpanded(isExpanded: Boolean) {
        if (!isExpanded) {
            _uiState.update { it.copy(searchTerm = "", searchedTravels = emptyList()) }
        }
        _uiState.update { it.copy(isSearchScreenExpanded = isExpanded) }
    }

    private fun updateSearchedTravels() {
        if (uiState.value.searchTerm.isBlank()) {
            _uiState.update { it.copy(searchedTravels = emptyList()) }
            return
        }
        val searchedTravels = uiState.value.travels.filter {
            it.travelName.lowercase()
                .contains(other = uiState.value.searchTerm, ignoreCase = true)
        }
        _uiState.update { it.copy(searchedTravels = searchedTravels) }
    }

    fun clearSearchQuery() {
        updateQuery("")
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(searchTerm = query) }
        updateSearchedTravels()
    }
}