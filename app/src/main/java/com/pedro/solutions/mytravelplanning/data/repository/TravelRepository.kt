package com.pedro.solutions.mytravelplanning.data.repository

import com.pedro.solutions.mytravelplanning.data.database.TravelDao
import com.pedro.solutions.mytravelplanning.data.database.TravelGuideWithDays
import com.pedro.solutions.mytravelplanning.data.database.insertTravelGuide
import com.pedro.solutions.mytravelplanning.data.database.updateTravelGuide
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

class TravelRepository(
    private val dao: TravelDao
) {
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

    suspend fun addTravelDate(
        travelId: Long,
        formattedStartDate: String,
        formattedEndDate: String
    ) {
        dao.addTravelDate(
            travelId,
            formattedStartDate,
            formattedEndDate
        )
    }
}