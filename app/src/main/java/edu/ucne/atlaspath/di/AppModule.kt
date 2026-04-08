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
import edu.ucne.atlaspath.BuildConfig
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.dataBase.AppDataBase
import edu.ucne.atlaspath.data.remote.ExerciseDbApi
import edu.ucne.atlaspath.data.remote.GeminiApi
import edu.ucne.atlaspath.data.repository.RoutineRepositoryImpl
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "atlaspath.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRoutineDao(db: AppDataBase): RoutineDao = db.routineDao()

    @Provides
    fun provideSessionDao(db: AppDataBase): SessionDao = db.sessionDao()

    @Provides
    @Singleton
    fun provideRoutineRepository(
        routineDao: RoutineDao,
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
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-RapidAPI-Key", BuildConfig.EXERCISE_DB_KEY)
                    .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                    .build()
                chain.proceed(request)
            }.build()
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
    fun provideExerciseDbApi(moshi: Moshi, client: OkHttpClient): ExerciseDbApi {
        return Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .client(client) // Usamos el cliente con la llave oculta
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExerciseDbApi::class.java)
    }
}