package edu.ucne.atlaspath.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.dao.routineDao
import edu.ucne.atlaspath.data.local.dataBase.appDataBase
import edu.ucne.atlaspath.data.remote.GeminiApi
import edu.ucne.atlaspath.data.remote.ExerciseDbApi
import edu.ucne.atlaspath.data.repository.RoutineRepositoryImpl
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): appDataBase {
        return Room.databaseBuilder(
            context,
            appDataBase::class.java,
            "atlaspath.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRoutineDao(db: appDataBase): routineDao = db.routineDao()

    @Provides
    fun provideSessionDao(db: appDataBase): SessionDao = db.sessionDao()

    @Provides
    @Singleton
    fun provideRoutineRepository(
        routineDao: routineDao,
        sessionDao: SessionDao
    ): RoutineRepository {
        return RoutineRepositoryImpl(routineDao, sessionDao)
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApi(moshi: Moshi): GeminiApi {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExerciseDbApi(moshi: Moshi): ExerciseDbApi {
        return Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExerciseDbApi::class.java)
    }
}