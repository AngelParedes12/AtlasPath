package edu.ucne.atlaspath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ucne.atlaspath.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM NutritionRecords ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM NutritionRecords WHERE dateString = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FoodEntity)

    @Query("DELETE FROM NutritionRecords WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}