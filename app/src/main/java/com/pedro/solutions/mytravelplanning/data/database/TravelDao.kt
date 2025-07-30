package com.pedro.solutions.mytravelplanning.data.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.pedro.solutions.mytravelplanning.data.database.entities.DayEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.TravelGuideEntity

data class TravelWithDays(
    @Embedded val travel: TravelGuideEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "travelId"
    )
    val days: List<DayEntity>
)

@Dao
interface TravelDao {

    @Transaction
    @Query("SELECT * FROM travel_guides")
    suspend fun getAllTravels(): List<TravelWithDays>

    @Transaction
    @Query("SELECT * FROM days WHERE travelId = :travelId")
    suspend fun getDaysAndTravelActivities(travelId: Long): List<DayEntity>

    @Insert
    suspend fun insertTravel(travel: TravelGuideEntity): Long

    @Insert
    suspend fun insertDays(days: List<DayEntity>)
}