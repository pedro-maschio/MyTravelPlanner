package com.pedro.solutions.mytravelplanning.screens.main

import com.pedro.solutions.mytravelplanning.CoroutineTestRule
import com.pedro.solutions.mytravelplanning.data.database.TravelGuideWithDays
import com.pedro.solutions.mytravelplanning.data.database.entities.TravelGuideEntity
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {
    private val travelRepository = mockk<TravelRepository>()
    private val viewModel = MainScreenViewModel(travelRepository)

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Test
    fun whenGoBackEmitGoBackEvent() = runTest {
        var lastEvent: MainScreenUiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect {
                lastEvent = it
            }
        }
        viewModel.goBack()
        advanceUntilIdle()
        job.cancel()

        assertTrue(lastEvent is MainScreenUiEvent.GoBack)
    }

    @Test
    fun whenOpenTravelCreationEmitOpenTravelCreation() = runTest {
        var lastEvent: MainScreenUiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect {
                lastEvent = it
            }
        }
        viewModel.openCreateTravelScreen(TRAVEL_ID)
        advanceUntilIdle()
        job.cancel()
        assertTrue(lastEvent is MainScreenUiEvent.OpenCreateTravelScreen)
    }

    @Test
    fun whenCloseSearchScreenItShouldClearSearchTermAndSearchResults() = runTest {
        coEvery {
            travelRepository.loadTravels()
        } returns getTravelGuideWithDays()
        viewModel.loadTravels()
        advanceUntilIdle()
        viewModel.updateQuery(SEARCH_TERM_EXAMPLE)
        advanceUntilIdle()
        viewModel.setSearchScreenExpanded(false)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchTerm.isEmpty())
        assertTrue(viewModel.uiState.value.searchedTravels.isEmpty())
    }

    @Test
    fun whenClickClearButtonItShouldClearSearchTerm() = runTest {
        viewModel.updateQuery(SEARCH_TERM_EXAMPLE)
        advanceUntilIdle()
        viewModel.clearSearchQuery()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchTerm.isEmpty())
    }

    @Test
    fun whenSearchTermIsEmptyItShouldClearSearchedTravels() = runTest {
        coEvery {
            travelRepository.loadTravels()
        } returns getTravelGuideWithDays()
        viewModel.loadTravels()
        advanceUntilIdle()
        viewModel.updateQuery("")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchedTravels.isEmpty())
    }

    @Test
    fun whenIsOnSelectionModeItShouldSetSelectionModeAndClearSelectedTravels() = runTest {
        coEvery {
            travelRepository.loadTravels()
        } returns getTravelGuideWithDays()
        viewModel.loadTravels()
        advanceUntilIdle()
        viewModel.selectTravel(TRAVEL_ID)
        advanceUntilIdle()
        viewModel.setOnSelectionMode(true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.selectedTravelIds.isEmpty())
        assertTrue(viewModel.uiState.value.isOnSelectionMode)
        assertTrue(viewModel.uiState.value.travels.all { !(it.isSelected) })
    }

    @Test
    fun whenDeleteTravelItShouldHideDropdownExitSelectionModeLoadTravelsAndDeleteTravel() =
        runTest {
            coEvery {
                travelRepository.loadTravels()
            } returns getTravelGuideWithDays()
            coEvery {
                travelRepository.deleteTravel(TRAVEL_ID)
            } returns Unit

            viewModel.loadTravels()
            advanceUntilIdle()
            viewModel.setDropdownMenuShowing(true)
            advanceUntilIdle()
            viewModel.selectTravel(TRAVEL_ID)
            advanceUntilIdle()
            viewModel.onDeleteTravelsClick()
            advanceUntilIdle()


            assertFalse(viewModel.uiState.value.isDropDownMenuShowing)
            assertFalse(viewModel.uiState.value.isOnSelectionMode)
            coVerify(exactly = 2) {
                travelRepository.loadTravels()
            }
            coVerify(exactly = 1) {
                travelRepository.deleteTravel(TRAVEL_ID)
            }
        }

    private companion object {
        const val TRAVEL_ID = 0L
        const val SEARCH_TERM_EXAMPLE = "example"

        fun getTravelGuideWithDays() = listOf(
            TravelGuideWithDays(
                travelGuide = TravelGuideEntity(
                    id = 0L,
                    "example",
                    0L,
                    0L
                ), days = emptyList()
            )
        )
    }
}