package com.pedro.solutions.mytravelplanning.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pedro.solutions.mytravelplanning.data.database.entities.DayEntity
import com.pedro.solutions.mytravelplanning.data.database.entities.TravelGuideEntity

@Database(
    entities = [TravelGuideEntity::class, DayEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {

    abstract fun travelDao(): TravelDao
}