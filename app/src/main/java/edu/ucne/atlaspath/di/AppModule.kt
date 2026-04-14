package edu.ucne.atlaspath.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.atlaspath.data.local.dao.ExerciseDao
import edu.ucne.atlaspath.data.local.dao.FoodDao
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.dataBase.AppDataBase
import edu.ucne.atlaspath.data.local.entity.ExerciseEntity
import edu.ucne.atlaspath.data.remote.GeminiApi
import edu.ucne.atlaspath.data.remote.remoteDataSource.GeminiRemoteDataSource
import edu.ucne.atlaspath.data.repository.NutritionRepositoryImpl
import edu.ucne.atlaspath.data.repository.RoutineRepositoryImpl
import edu.ucne.atlaspath.domain.repository.NutritionRepository
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        exerciseDaoProvider: Provider<ExerciseDao>
    ): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "atlaspath.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = exerciseDaoProvider.get()
                        dao.insertAll(obtenerEjerciciosBase())
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideRoutineDao(db: AppDataBase): RoutineDao = db.routineDao()

    @Provides
    fun provideSessionDao(db: AppDataBase): SessionDao = db.sessionDao()

    @Provides
    fun provideExerciseDao(db: AppDataBase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideFoodDao(db: AppDataBase): FoodDao = db.foodDao()

    @Provides
    @Singleton
    fun provideRoutineRepository(
        routineDao: RoutineDao,
        sessionDao: SessionDao,
        exerciseDao: ExerciseDao
    ): RoutineRepository {
        return RoutineRepositoryImpl(routineDao, sessionDao, exerciseDao)
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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApi(moshi: Moshi, client: OkHttpClient): GeminiApi {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    private fun obtenerEjerciciosBase(): List<ExerciseEntity> {
        return listOf(
            // PECHO
            ExerciseEntity("1", "Press de Banca con Barra", "Pecho", "Pecho", equipment = "Barra"),
            ExerciseEntity("2", "Press de Banca con Mancuernas", "Pecho", "Pecho", equipment = "Mancuernas"),
            ExerciseEntity("3", "Press Inclinado con Barra", "Pecho", "Pecho", equipment = "Barra"),
            ExerciseEntity("4", "Press Inclinado con Mancuernas", "Pecho", "Pecho", equipment = "Mancuernas"),
            ExerciseEntity("5", "Press Declinado con Barra", "Pecho", "Pecho", equipment = "Barra"),
            ExerciseEntity("6", "Aperturas con Mancuernas", "Pecho", "Pecho", equipment = "Mancuernas"),
            ExerciseEntity("7", "Aperturas en Máquina (Pec Deck)", "Pecho", "Pecho", equipment = "Máquina"),
            ExerciseEntity("8", "Cruce de Poleas Altas", "Pecho", "Pecho", equipment = "Polea"),
            ExerciseEntity("9", "Cruce de Poleas Bajas", "Pecho", "Pecho", equipment = "Polea"),
            ExerciseEntity("10", "Flexiones (Push-ups)", "Pecho", "Pecho", equipment = "Peso Corporal"),
            ExerciseEntity("11", "Fondos en Paralelas (Dips)", "Pecho/Tríceps", "Brazos", equipment = "Peso Corporal"),
            ExerciseEntity("12", "Pullover con Mancuerna", "Pecho/Espalda", "Pecho", equipment = "Mancuernas"),

            // ESPALDA
            ExerciseEntity("13", "Dominadas (Pull-ups)", "Espalda", "Espalda", equipment = "Peso Corporal"),
            ExerciseEntity("14", "Dominadas Supinas (Chin-ups)", "Espalda/Bíceps", "Espalda", equipment = "Peso Corporal"),
            ExerciseEntity("15", "Jalón al Pecho en Polea", "Espalda", "Espalda", equipment = "Polea"),
            ExerciseEntity("16", "Jalón tras Nuca", "Espalda", "Espalda", equipment = "Polea"),
            ExerciseEntity("17", "Remo con Barra", "Espalda", "Espalda", equipment = "Barra"),
            ExerciseEntity("18", "Remo con Mancuerna a una Mano", "Espalda", "Espalda", equipment = "Mancuernas"),
            ExerciseEntity("19", "Remo en Máquina (Sentado)", "Espalda", "Espalda", equipment = "Máquina"),
            ExerciseEntity("20", "Remo al Cuello (Polea o Barra)", "Espalda/Trapecio", "Espalda", equipment = "Polea"),
            ExerciseEntity("21", "Pull-over en Polea Alta", "Espalda", "Espalda", equipment = "Polea"),
            ExerciseEntity("22", "Peso Muerto Convencional", "Espalda/Piernas", "Espalda", equipment = "Barra"),
            ExerciseEntity("23", "Peso Muerto Rumano", "Isquiotibiales/Espalda", "Espalda", equipment = "Barra"),
            ExerciseEntity("24", "Hiperextensiones (Lumbares)", "Espalda Baja", "Espalda", equipment = "Peso Corporal"),

            // HOMBROS
            ExerciseEntity("25", "Press Militar con Barra", "Hombros", "Hombros", equipment = "Barra"),
            ExerciseEntity("26", "Press Militar con Mancuernas", "Hombros", "Hombros", equipment = "Mancuernas"),
            ExerciseEntity("27", "Press Arnold", "Hombros", "Hombros", equipment = "Mancuernas"),
            ExerciseEntity("28", "Elevaciones Laterales con Mancuernas", "Hombros", "Hombros", equipment = "Mancuernas"),
            ExerciseEntity("29", "Elevaciones Laterales en Polea", "Hombros", "Hombros", equipment = "Polea"),
            ExerciseEntity("30", "Elevaciones Frontales con Mancuernas", "Hombros", "Hombros", equipment = "Mancuernas"),
            ExerciseEntity("31", "Elevaciones Frontales con Barra", "Hombros", "Hombros", equipment = "Barra"),
            ExerciseEntity("32", "Pájaro (Aperturas Posteriores)", "Hombro Posterior", "Hombros", equipment = "Mancuernas"),
            ExerciseEntity("33", "Face Pull en Polea", "Hombro Posterior/Rotadores", "Hombros", equipment = "Polea"),
            ExerciseEntity("34", "Encogimientos de Hombros (Trapecio)", "Trapecio", "Hombros", equipment = "Barra/Mancuernas"),
            ExerciseEntity("35", "Press Tras Nuca con Barra", "Hombros", "Hombros", equipment = "Barra"),

            // BÍCEPS
            ExerciseEntity("36", "Curl de Bíceps con Barra", "Bíceps", "Brazos", equipment = "Barra"),
            ExerciseEntity("37", "Curl de Bíceps con Mancuernas", "Bíceps", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("38", "Curl Martillo", "Bíceps/Braquial", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("39", "Curl Predicador con Barra Z", "Bíceps", "Brazos", equipment = "Barra Z"),
            ExerciseEntity("40", "Curl Predicador con Mancuerna", "Bíceps", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("41", "Curl Concentrado", "Bíceps", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("42", "Curl en Polea Baja", "Bíceps", "Brazos", equipment = "Polea"),
            ExerciseEntity("43", "Curl en Polea Alta (Curl Araña)", "Bíceps", "Brazos", equipment = "Polea"),

            // TRÍCEPS
            ExerciseEntity("44", "Extensión de Tríceps en Polea", "Tríceps", "Brazos", equipment = "Polea"),
            ExerciseEntity("45", "Extensión de Tríceps con Mancuerna a dos Manos", "Tríceps", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("46", "Extensión de Tríceps con Barra (Press Francés)", "Tríceps", "Brazos", equipment = "Barra Z"),
            ExerciseEntity("47", "Patada de Tríceps con Mancuerna", "Tríceps", "Brazos", equipment = "Mancuernas"),
            ExerciseEntity("48", "Fondos en Banco (Tríceps)", "Tríceps", "Brazos", equipment = "Peso Corporal"),
            ExerciseEntity("49", "Press de Banca con Agarre Cerrado", "Tríceps/Pecho", "Brazos", equipment = "Barra"),
            ExerciseEntity("50", "Extensión de Tríceps en Polea con Cuerda", "Tríceps", "Brazos", equipment = "Polea"),

            // PIERNAS (Cuádriceps / Glúteos / Isquios)
            ExerciseEntity("51", "Sentadilla con Barra (Back Squat)", "Piernas/Glúteos", "Piernas", equipment = "Barra"),
            ExerciseEntity("52", "Sentadilla Frontal", "Cuádriceps", "Piernas", equipment = "Barra"),
            ExerciseEntity("53", "Prensa de Piernas", "Piernas", "Piernas", equipment = "Máquina"),
            ExerciseEntity("54", "Hack Squat", "Cuádriceps", "Piernas", equipment = "Máquina"),
            ExerciseEntity("55", "Extensiones de Cuádriceps en Máquina", "Cuádriceps", "Piernas", equipment = "Máquina"),
            ExerciseEntity("56", "Zancadas (Lunges) con Mancuernas", "Piernas/Glúteos", "Piernas", equipment = "Mancuernas"),
            ExerciseEntity("57", "Zancadas con Barra", "Piernas/Glúteos", "Piernas", equipment = "Barra"),
            ExerciseEntity("58", "Peso Muerto Rumano", "Isquiotibiales", "Piernas", equipment = "Barra"),
            ExerciseEntity("59", "Curl de Femoral en Máquina", "Isquiotibiales", "Piernas", equipment = "Máquina"),
            ExerciseEntity("60", "Curl de Femoral de Pie", "Isquiotibiales", "Piernas", equipment = "Máquina"),
            ExerciseEntity("61", "Hip Thrust con Barra", "Glúteos", "Piernas", equipment = "Barra"),
            ExerciseEntity("62", "Puente de Glúteos en Suelo", "Glúteos", "Piernas", equipment = "Peso Corporal"),
            ExerciseEntity("63", "Patada de Glúteo en Polea", "Glúteos", "Piernas", equipment = "Polea"),
            ExerciseEntity("64", "Abducción de Cadera en Máquina", "Glúteo Medio", "Piernas", equipment = "Máquina"),
            ExerciseEntity("65", "Aducción de Cadera en Máquina", "Aductores", "Piernas", equipment = "Máquina"),
            ExerciseEntity("66", "Sentadilla Búlgara", "Piernas/Glúteos", "Piernas", equipment = "Mancuernas/Peso Corporal"),

            // PANTORRILLAS
            ExerciseEntity("67", "Elevación de Pantorrillas de Pie", "Pantorrillas", "Piernas", equipment = "Máquina"),
            ExerciseEntity("68", "Elevación de Pantorrillas Sentado", "Sóleo", "Piernas", equipment = "Máquina"),
            ExerciseEntity("69", "Elevación de Pantorrillas en Prensa", "Pantorrillas", "Piernas", equipment = "Máquina"),
            ExerciseEntity("70", "Burpees", "Full Body", "Cardio", equipment = "Peso Corporal"),

            // ABDOMEN / CORE
            ExerciseEntity("71", "Plancha (Plank)", "Core", "Abdomen", equipment = "Peso Corporal"),
            ExerciseEntity("72", "Plancha Lateral", "Oblicuos", "Abdomen", equipment = "Peso Corporal"),
            ExerciseEntity("73", "Crunch Abdominal en Suelo", "Recto Abdominal", "Abdomen", equipment = "Peso Corporal"),
            ExerciseEntity("74", "Crunch en Máquina", "Recto Abdominal", "Abdomen", equipment = "Máquina"),
            ExerciseEntity("75", "Elevaciones de Piernas Colgado", "Abdominales Inferiores", "Abdomen", equipment = "Peso Corporal"),
            ExerciseEntity("76", "Elevaciones de Piernas en Suelo", "Abdominales Inferiores", "Abdomen", equipment = "Peso Corporal"),
            ExerciseEntity("77", "Giros Rusos con Peso", "Oblicuos", "Abdomen", equipment = "Mancuernas/Disco"),
            ExerciseEntity("78", "Rueda Abdominal", "Core", "Abdomen", equipment = "Rueda"),
            ExerciseEntity("79", "Mountain Climbers", "Core/Cardio", "Abdomen", equipment = "Peso Corporal"),

            // FUNCIONAL / OTROS
            ExerciseEntity("80", "Kettlebell Swing", "Full Body", "Cardio", equipment = "Kettlebell"),
            ExerciseEntity("81", "Peso Muerto Sumo", "Piernas/Espalda", "Piernas", equipment = "Barra"),
            ExerciseEntity("82", "Snatch con Mancuerna", "Full Body", "Olímpico", equipment = "Mancuernas"),
            ExerciseEntity("83", "Clean and Press", "Full Body", "Olímpico", equipment = "Barra"),
            ExerciseEntity("84", "Box Jumps", "Piernas", "Pliométrico", equipment = "Cajón"),
            ExerciseEntity("85", "Saltos a la Comba", "Full Body", "Cardio", equipment = "Comba"),
            ExerciseEntity("86", "Battle Ropes", "Full Body", "Cardio", equipment = "Cuerdas"),
            ExerciseEntity("87", "Remo en Máquina (Ergómetro)", "Full Body", "Cardio", equipment = "Máquina"),
        )
    }

    @Provides
    @Singleton
    fun provideNutritionRepository(
        foodDao: FoodDao,
        aiDataSource: GeminiRemoteDataSource
    ): NutritionRepository {
        return NutritionRepositoryImpl(foodDao, aiDataSource)
    }
}