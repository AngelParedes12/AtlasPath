package edu.ucne.atlaspath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NutritionRecords")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodText: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val dateString: String,
    val timestamp: Long
)