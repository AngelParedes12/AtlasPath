package edu.ucne.atlaspath.data.local.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.atlaspath.data.local.dao.ExerciseDao
import edu.ucne.atlaspath.data.local.dao.FoodDao
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.entity.ExerciseEntity
import edu.ucne.atlaspath.data.local.entity.FoodEntity
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.data.local.entity.SessionEntity

@Database(
    entities = [RoutineEntity::class, SessionEntity::class, ExerciseEntity::class, FoodEntity::class],
    version =1,
    exportSchema = false
)
@TypeConverters(AtlasPathConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun sessionDao(): SessionDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun foodDao(): FoodDao
}