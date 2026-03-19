package edu.ucne.atlaspath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    // Crucial para UDF: La UI observará este Flow
    @Query("SELECT * FROM rutinas ORDER BY fechaCreacion DESC")
    fun observeAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM rutinas WHERE id = :id")
    suspend fun getRoutineById(id: String): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)
}