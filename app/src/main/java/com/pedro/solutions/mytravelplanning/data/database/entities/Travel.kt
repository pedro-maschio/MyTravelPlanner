package com.pedro.solutions.mytravelplanning.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pedro.solutions.mytravelplanning.data.models.openai.Day

@Entity(tableName = "travel")
data class Travel(
    @PrimaryKey val id: Int
)

@Entity(tableName = "travel_guides")
data class TravelGuideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)

@Entity(
    tableName = "days",
    foreignKeys = [
        ForeignKey(
            entity = TravelGuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["travelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("travelId")]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val travelId: Long,
    val title: String?,
    val activities: List<String>? // Requires TypeConverter
)