package edu.ucne.atlaspath.data.local.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.data.local.entity.SessionEntity

@Database(
    entities = [RoutineEntity::class, SessionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(AtlasPathConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun sessionDao(): SessionDao
}