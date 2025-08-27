package com.pedro.solutions.mytravelplanning.screens.create

import com.pedro.solutions.mytravelplanning.CoroutineTestRule
import com.pedro.solutions.mytravelplanning.data.database.DayWithActivities
import com.pedro.solutions.mytravelplanning.data.database.TravelGuideWithDays
import com.pedro.solutions.mytravelplanning.data.database.entities.ActivityEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.DayEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.TravelGuideEntity
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTravelViewModelTest {
    private val travelRepository = mockk<TravelRepository>()
    private val viewModel = CreateTravelViewModel(travelRepository)

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Test
    fun loadTraveDetailPopulateUiState() = runTest {
        coEvery {
            travelRepository.loadTravelDetail(any())

        } returns getTravelGuideWithDays()

        viewModel.loadTravel(TRAVEL_ID)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(TRAVEL_NAME, viewModel.uiState.value.travel.travelName)
        assertEquals(1, viewModel.uiState.value.travel.days.size)
        assertEquals(TRAVEL_TIMESTAMP, viewModel.uiState.value.travel.createdAt)
        assertEquals(TRAVEL_TIMESTAMP, viewModel.uiState.value.travel.updatedAt)
        assertEquals("26/08/2025", viewModel.uiState.value.travel.formattedStartDate)
        assertEquals("29/08/2025", viewModel.uiState.value.travel.formattedEndDate)
        assertEquals(getTravels(), viewModel.uiState.value.travels)
    }

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

    @Test
    fun showLoadingUpdatesIsLoadingToTrue() = runTest {
        viewModel.showLoading()
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun hideLoadingUpdatesIsLoadingToFalse() = runTest {
        viewModel.showLoading()
        assertTrue(viewModel.uiState.value.isLoading)
        viewModel.hideLoading()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun updateTravelNameUpdatesNameInUiStateAndTravelObject() = runTest {
        val newName = "Vacation in Paris"
        viewModel.updateTravelName(newName)
        assertEquals(newName, viewModel.uiState.value.travelName)
        assertEquals(newName, viewModel.uiState.value.travel.travelName)
    }

    @Test
    fun addActivityAddsActivityToSpecifiedDay() = runTest {
        viewModel.addDay()
        viewModel.addActivity(index = 0)

        val day = viewModel.uiState.value.travel.days.getOrNull(0)
        Assert.assertNotNull(day)
        assertEquals(1, day?.activities?.size)
        assertEquals("", day?.activities?.getOrNull(0))

        val activityInTravels = viewModel.uiState.value.travels.find {
            it is TravelType.Activity && it.dayIndex == 0 && it.index == 0
        }
        Assert.assertNotNull(activityInTravels)
    }

    @Test
    fun updateTravelDayTextUpdatesCorrectDayTitle() = runTest {
        viewModel.addDay() // Day 1
        viewModel.addDay()      // Day 2
        val newTitle = "First Day Adventures"
        viewModel.updateTravelDayText(index = 0, newText = newTitle)

        val state = viewModel.uiState.value
        assertEquals(newTitle, state.travel.days[0]?.title)
        assertEquals("Day 2", state.travel.days[1]?.title)

        val dayInTravels =
            state.travels.find { it is TravelType.Day && it.index == 0 } as? TravelType.Day
        assertEquals(newTitle, dayInTravels?.title)
    }

    @Test
    fun updateTravelActivityTextUpdatesCorrectActivityTitle() = runTest {
        viewModel.addDay()
        viewModel.addActivity(index = 0)
        val newActivityText = "Visit Eiffel Tower"
        viewModel.updateTravelActivityText(
            dayIndex = 0,
            activityIndex = 0,
            newText = newActivityText
        )

        val state = viewModel.uiState.value
        assertEquals(newActivityText, state.travel.days[0]?.activities?.get(0))

        val activityInTravels = state.travels.find {
            it is TravelType.Activity && it.dayIndex == 0 && it.index == 0
        } as? TravelType.Activity
        assertEquals(newActivityText, activityInTravels?.title)
    }

    @Test
    fun setEditingStateUpdatesIsEditingState() = runTest {
        viewModel.setEditingState(true)
        assertTrue(viewModel.uiState.value.isEditing)

        viewModel.setEditingState(false)
        assertFalse(viewModel.uiState.value.isEditing)
    }

    private companion object {
        const val TRAVEL_ID = 0L
        const val TRAVEL_NAME = "Just an example"
        const val TRAVEL_TIMESTAMP = 968025600L// My birthday!

        fun getTravelGuideWithDays() =
            TravelGuideWithDays(
                travelGuide = TravelGuideEntity(
                    id = TRAVEL_ID,
                    travelName = TRAVEL_NAME,
                    formattedStartDate = "26/08/2025",
                    formattedEndDate = "29/08/2025",
                    createdAt = TRAVEL_TIMESTAMP,
                    updatedAt = TRAVEL_TIMESTAMP,
                ),
                days = listOf(
                    DayWithActivities(
                        day = DayEntity(0L, "Day 1", 0L),
                        activities = listOf(ActivityEntity(0L, "Activity 1", 0L))
                    )
                )
            )

        fun getTravels() =
            listOf(TravelType.Day(0, "Day 1"), TravelType.Activity(0, 0, "Activity 1"))
    }
}