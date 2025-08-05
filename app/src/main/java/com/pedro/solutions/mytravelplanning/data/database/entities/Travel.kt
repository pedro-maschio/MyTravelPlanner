package com.pedro.solutions.mytravelplanning.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class TravelGuideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val travelName: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TravelGuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["travelGuideId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("travelGuideId")]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String?,
    val travelGuideId: Long
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId")]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dayId: Long
)