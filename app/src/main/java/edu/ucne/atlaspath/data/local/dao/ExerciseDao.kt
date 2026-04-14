package edu.ucne.atlaspath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ucne.atlaspath.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM Exercises 
        WHERE name LIKE '%' || :query || '%' 
        OR target LIKE '%' || :query || '%' 
        OR bodyPart LIKE '%' || :query || '%'
    """)
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM Exercises WHERE target = :muscle OR bodyPart = :muscle")
    fun getExercisesByMuscle(muscle: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("SELECT COUNT(id) FROM Exercises")
    suspend fun countExercises(): Int
}