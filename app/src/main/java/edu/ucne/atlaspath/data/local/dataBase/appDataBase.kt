package edu.ucne.atlaspath.data.local.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.dao.routineDao
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.data.local.entity.SessionEntity

@Database(
    entities = [RoutineEntity::class, SessionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(AtlasPathConverters::class)
abstract class appDataBase : RoomDatabase() {
    abstract fun routineDao(): routineDao
    abstract fun sessionDao(): SessionDao
}