package edu.ucne.atlaspath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.entity.RoutineEntity

@Database(entities = [RoutineEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Necesario para la lista de ejercicios
abstract class AtlasPathDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}