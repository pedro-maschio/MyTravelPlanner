package com.pedro.solutions.mytravelplanning.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.pedro.solutions.mytravelplanning.BuildConfig
import com.pedro.solutions.mytravelplanning.data.database.TravelDao
import com.pedro.solutions.mytravelplanning.data.database.TravelGuideWithDays
import com.pedro.solutions.mytravelplanning.data.database.insertTravelGuide
import com.pedro.solutions.mytravelplanning.data.database.updateTravelGuide
import com.pedro.solutions.mytravelplanning.data.models.ChatRequest
import com.pedro.solutions.mytravelplanning.data.models.ChatResponse
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.network.ChatGptApi
import com.pedro.solutions.mytravelplanning.ui.screens.intro.SelectedOption

class TravelRepository(
    private val context: Context,
    private val api: ChatGptApi,
    private val dao: TravelDao
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun getChatResponse(chatRequest: ChatRequest): ChatResponse =
        api.getChatResponse("Bearer ${BuildConfig.CHAT_GPT_API_KEY}", chatRequest)

    fun isShownIntroduction() = getSavedSelectedVehicle() != SelectedOption.NONE.name

    fun saveSelectedVehicle(selectedOption: SelectedOption) {
        with(sharedPreferences.edit()) {
            putString(SELECTED_VEHICLE_KEY, selectedOption.name)
            apply()
        }
    }

    fun getSavedSelectedVehicle() =
        sharedPreferences.getString(SELECTED_VEHICLE_KEY, SelectedOption.NONE.name)

    suspend fun insertTravelGuide(travelGuide: TravelGuide) {
        dao.insertTravelGuide(travelGuide)
    }

    suspend fun loadTravels(): List<TravelGuideWithDays> {
        return dao.getAllTravels()
    }

    suspend fun loadTravelDetail(travelId: Long): TravelGuideWithDays {
        return dao.getTravelGuideWithDays(travelId)
    }

    suspend fun updateTravelGuide(travelId: Long, travelGuide: TravelGuide) {
        dao.updateTravelGuide(travelGuideId = travelId, travelGuide = travelGuide)
    }

    suspend fun deleteTravel(travelId: Long) {
        dao.deleteTravelGuideEntity(travelId)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "user_prefs"

        private const val SELECTED_VEHICLE_KEY = "selected_vehicle"
    }
}