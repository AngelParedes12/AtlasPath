package edu.ucne.atlaspath.data.local.dataBase

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.ucne.atlaspath.data.local.entity.EjercicioEntity
import edu.ucne.atlaspath.data.local.entity.RegistroEntity

class AtlasPathConverters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val ejercicioListType = Types.newParameterizedType(List::class.java, EjercicioEntity::class.java)
    private val ejercicioAdapter = moshi.adapter<List<EjercicioEntity>>(ejercicioListType)

    @TypeConverter
    fun fromEjercicioList(value: List<EjercicioEntity>?): String {
        return value?.let { ejercicioAdapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toEjercicioList(value: String): List<EjercicioEntity> {
        return try {
            ejercicioAdapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private val registroListType = Types.newParameterizedType(List::class.java, RegistroEntity::class.java)
    private val registroAdapter = moshi.adapter<List<RegistroEntity>>(registroListType)

    @TypeConverter
    fun fromRegistroList(value: List<RegistroEntity>?): String {
        return value?.let { registroAdapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toRegistroList(value: String): List<RegistroEntity> {
        return try {
            registroAdapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}