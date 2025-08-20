package com.pedro.solutions.mytravelplanning.screens.create

import com.pedro.solutions.mytravelplanning.CoroutineTestRule
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTravelViewModelTest {
    private val travelRepository = mockk<TravelRepository>()
    private val viewModel = CreateTravelViewModel(travelRepository)

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Test
    fun whenCreateTravelItShouldEmitCreateTravelEvent() = runTest {
        coEvery {
            travelRepository.insertTravelGuide(any())
        } returns Unit

        var lastEvent: CreateTravelUiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect {
                lastEvent = it
            }
        }
        viewModel.createTravel()
        advanceUntilIdle()
        job.cancel()

        println(lastEvent)

        assert(lastEvent is CreateTravelUiEvent.OnTravelCreated)
    }

    @Test
    fun whenCreateTravelItShouldEmitDeleteTravelEvent() = runTest {
        var lastEvent: CreateTravelUiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect {
                lastEvent = it
            }
        }
        viewModel.deleteTravel()
        advanceUntilIdle()
        job.cancel()

        assert(lastEvent is CreateTravelUiEvent.OnTravelDeleted)
    }
}