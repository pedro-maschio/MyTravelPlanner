package com.pedro.solutions.mytravelplanning.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.pedro.solutions.mytravelplanning.data.database.entities.ActivityEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.DayEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.TravelGuideEntity
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide

data class DayWithActivities(
    @Embedded val day: DayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dayId"
    )
    val activities: List<ActivityEntity>
)

data class TravelGuideWithDays(
    @Embedded val travelGuide: TravelGuideEntity,
    @Relation(
        entity = DayEntity::class,
        parentColumn = "id",
        entityColumn = "travelGuideId"
    )
    val days: List<DayWithActivities>
)

@Dao
interface TravelDao {
    @Transaction
    @Query("SELECT * FROM TravelGuideEntity")
    suspend fun getAllTravels(): List<TravelGuideWithDays>

    @Transaction
    @Query("SELECT * FROM TravelGuideEntity WHERE id = :id")
    suspend fun getTravelGuideWithDays(id: Long): TravelGuideWithDays

    @Insert
    suspend fun insertTravelGuide(travelGuide: TravelGuideEntity): Long

    @Update
    suspend fun updateTravelGuideEntity(travelGuide: TravelGuideEntity)

    @Update
    suspend fun updateDayEntities(days: List<DayEntity>)

    @Update
    suspend fun updateActivityEntities(activities: List<ActivityEntity>)

    @Transaction
    @Query("DELETE FROM TravelGuideEntity WHERE id = :id")
    suspend fun deleteTravelGuideEntity(id: Long)
    @Delete
    suspend fun deleteDayEntities(days: List<DayEntity>)

    @Delete
    suspend fun deleteActivityEntities(activities: List<ActivityEntity>)

    @Insert
    suspend fun insertDays(days: List<DayEntity>): List<Long>

    @Insert
    suspend fun insertActivities(activities: List<ActivityEntity>)
}

suspend fun TravelDao.updateTravelGuide(
    travelGuideId: Long,
    travelGuide: TravelGuide
) {
    updateTravelGuideEntity(
        TravelGuideEntity(
            travelName = travelGuide.travelName,
            id = travelGuideId
        )
    )
    val existingGuide = getAllTravels().firstOrNull { it.travelGuide.id == travelGuideId }
    existingGuide?.let { deleteDayEntities(it.days.map { day -> day.day }) }

    val dayEntities = travelGuide.days
        .filterNotNull()
        .map { day -> DayEntity(title = day.title, travelGuideId = travelGuideId) }

    val dayIds = insertDays(dayEntities)

    dayIds.zip(travelGuide.days.filterNotNull()).forEach { (dayId, day) ->
        val activities = day.activities.map { activity ->
            ActivityEntity(title = activity, dayId = dayId)
        }
        insertActivities(activities)
    }
}

suspend fun TravelDao.insertTravelGuide(travelGuide: TravelGuide) {
    val travelGuideId = insertTravelGuide(TravelGuideEntity(travelName = travelGuide.travelName))

    val dayEntities = travelGuide.days
        .filterNotNull()
        .map { day ->
            DayEntity(
                title = day.title,
                travelGuideId = travelGuideId
            )
        }

    val dayIds = insertDays(dayEntities)

    dayIds.zip(travelGuide.days.filterNotNull()).forEach { (dayId, day) ->
        val activities = day.activities.map { activity ->
            ActivityEntity(
                title = activity,
                dayId = dayId
            )
        }
        insertActivities(activities)
    }
}