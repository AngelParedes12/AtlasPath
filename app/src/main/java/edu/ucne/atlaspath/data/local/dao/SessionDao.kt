package edu.ucne.atlaspath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ucne.atlaspath.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: SessionEntity)
    @Query("SELECT * FROM Sesiones ORDER BY fechaFin DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>
}