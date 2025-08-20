package com.pedro.solutions.mytravelplanning.screens.main

import com.pedro.solutions.mytravelplanning.CoroutineTestRule
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

    private companion object {
        const val TRAVEL_ID = 0L
    }
}