package edu.ucne.atlaspath.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Upsert
    suspend fun save(entity: RoutineEntity)

    @Query("DELETE FROM Rutinas WHERE rutinaId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM Rutinas WHERE rutinaId = :id")
    suspend fun find(id: Int): RoutineEntity?

    @Query("""
        SELECT * FROM Rutinas 
        WHERE (:query IS NULL OR titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%')
    """)
    fun getAll(query: String?): Flow<List<RoutineEntity>>
}