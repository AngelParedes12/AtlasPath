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

private object GymConstants {
    const val EQ_BARRA = "Barra"
    const val EQ_MANCUERNAS = "Mancuernas"
    const val EQ_MAQUINA = "Máquina"
    const val EQ_POLEA = "Polea"
    const val EQ_PESO_CORPORAL = "Peso Corporal"

    const val MUSCLE_PECHO = "Pecho"
    const val MUSCLE_ESPALDA = "Espalda"
    const val MUSCLE_HOMBROS = "Hombros"
    const val MUSCLE_BICEPS = "Bíceps"
    const val MUSCLE_TRICEPS = "Tríceps"
    const val MUSCLE_BRAZOS = "Brazos"
    const val MUSCLE_PIERNAS = "Piernas"
    const val MUSCLE_CUADRICEPS = "Cuádriceps"
    const val MUSCLE_GLUTEOS = "Glúteos"
    const val MUSCLE_ISQUIOS = "Isquiotibiales"
    const val MUSCLE_PIERNAS_GLUTEOS = "Piernas/Glúteos"
    const val MUSCLE_CORE = "Core"
    const val MUSCLE_ABDOMEN = "Abdomen"
    const val MUSCLE_FULL_BODY = "Full Body"
    const val MUSCLE_CARDIO = "Cardio"
}

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
            .baseUrl("[https://generativelanguage.googleapis.com/](https://generativelanguage.googleapis.com/)")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    private fun obtenerEjerciciosBase(): List<ExerciseEntity> {
        return listOf(
            // PECHO
            ExerciseEntity("1", "Press de Banca con Barra", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("2", "Press de Banca con Mancuernas", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("3", "Press Inclinado con Barra", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("4", "Press Inclinado con Mancuernas", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("5", "Press Declinado con Barra", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("6", "Aperturas con Mancuernas", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("7", "Aperturas en Máquina (Pec Deck)", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("8", "Cruce de Poleas Altas", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("9", "Cruce de Poleas Bajas", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("10", "Flexiones (Push-ups)", GymConstants.MUSCLE_PECHO, GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("11", "Fondos en Paralelas (Dips)", "${GymConstants.MUSCLE_PECHO}/${GymConstants.MUSCLE_TRICEPS}", GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("12", "Pullover con Mancuerna", "${GymConstants.MUSCLE_PECHO}/${GymConstants.MUSCLE_ESPALDA}", GymConstants.MUSCLE_PECHO, equipment = GymConstants.EQ_MANCUERNAS),

            // ESPALDA
            ExerciseEntity("13", "Dominadas (Pull-ups)", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("14", "Dominadas Supinas (Chin-ups)", "${GymConstants.MUSCLE_ESPALDA}/${GymConstants.MUSCLE_BICEPS}", GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("15", "Jalón al Pecho en Polea", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("16", "Jalón tras Nuca", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("17", "Remo con Barra", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("18", "Remo con Mancuerna a una Mano", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("19", "Remo en Máquina (Sentado)", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("20", "Remo al Cuello (Polea o Barra)", "${GymConstants.MUSCLE_ESPALDA}/Trapecio", GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("21", "Pull-over en Polea Alta", GymConstants.MUSCLE_ESPALDA, GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("22", "Peso Muerto Convencional", "${GymConstants.MUSCLE_ESPALDA}/${GymConstants.MUSCLE_PIERNAS}", GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("23", "Peso Muerto Rumano", "${GymConstants.MUSCLE_ISQUIOS}/${GymConstants.MUSCLE_ESPALDA}", GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("24", "Hiperextensiones (Lumbares)", "Espalda Baja", GymConstants.MUSCLE_ESPALDA, equipment = GymConstants.EQ_PESO_CORPORAL),

            // HOMBROS
            ExerciseEntity("25", "Press Militar con Barra", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("26", "Press Militar con Mancuernas", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("27", "Press Arnold", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("28", "Elevaciones Laterales con Mancuernas", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("29", "Elevaciones Laterales en Polea", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("30", "Elevaciones Frontales con Mancuernas", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("31", "Elevaciones Frontales con Barra", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("32", "Pájaro (Aperturas Posteriores)", "Hombro Posterior", GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("33", "Face Pull en Polea", "Hombro Posterior/Rotadores", GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("34", "Encogimientos de Hombros (Trapecio)", "Trapecio", GymConstants.MUSCLE_HOMBROS, equipment = "${GymConstants.EQ_BARRA}/${GymConstants.EQ_MANCUERNAS}"),
            ExerciseEntity("35", "Press Tras Nuca con Barra", GymConstants.MUSCLE_HOMBROS, GymConstants.MUSCLE_HOMBROS, equipment = GymConstants.EQ_BARRA),

            // BÍCEPS
            ExerciseEntity("36", "Curl de Bíceps con Barra", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("37", "Curl de Bíceps con Mancuernas", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("38", "Curl Martillo", "${GymConstants.MUSCLE_BICEPS}/Braquial", GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("39", "Curl Predicador con Barra Z", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = "Barra Z"),
            ExerciseEntity("40", "Curl Predicador con Mancuerna", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("41", "Curl Concentrado", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("42", "Curl en Polea Baja", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("43", "Curl en Polea Alta (Curl Araña)", GymConstants.MUSCLE_BICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_POLEA),

            // TRÍCEPS
            ExerciseEntity("44", "Extensión de Tríceps en Polea", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("45", "Extensión de Tríceps con Mancuerna a dos Manos", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("46", "Extensión de Tríceps con Barra (Press Francés)", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = "Barra Z"),
            ExerciseEntity("47", "Patada de Tríceps con Mancuerna", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("48", "Fondos en Banco (Tríceps)", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("49", "Press de Banca con Agarre Cerrado", "${GymConstants.MUSCLE_TRICEPS}/${GymConstants.MUSCLE_PECHO}", GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("50", "Extensión de Tríceps en Polea con Cuerda", GymConstants.MUSCLE_TRICEPS, GymConstants.MUSCLE_BRAZOS, equipment = GymConstants.EQ_POLEA),

            // PIERNAS
            ExerciseEntity("51", "Sentadilla con Barra (Back Squat)", GymConstants.MUSCLE_PIERNAS_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("52", "Sentadilla Frontal", GymConstants.MUSCLE_CUADRICEPS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("53", "Prensa de Piernas", GymConstants.MUSCLE_PIERNAS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("54", "Hack Squat", GymConstants.MUSCLE_CUADRICEPS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("55", "Extensiones de Cuádriceps en Máquina", GymConstants.MUSCLE_CUADRICEPS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("56", "Zancadas (Lunges) con Mancuernas", GymConstants.MUSCLE_PIERNAS_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("57", "Zancadas con Barra", GymConstants.MUSCLE_PIERNAS_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("58", "Peso Muerto Rumano", GymConstants.MUSCLE_ISQUIOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("59", "Curl de Femoral en Máquina", GymConstants.MUSCLE_ISQUIOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("60", "Curl de Femoral de Pie", GymConstants.MUSCLE_ISQUIOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("61", "Hip Thrust con Barra", GymConstants.MUSCLE_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("62", "Puente de Glúteos en Suelo", GymConstants.MUSCLE_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("63", "Patada de Glúteo en Polea", GymConstants.MUSCLE_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_POLEA),
            ExerciseEntity("64", "Abducción de Cadera en Máquina", "Glúteo Medio", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("65", "Aducción de Cadera en Máquina", "Aductores", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("66", "Sentadilla Búlgara", GymConstants.MUSCLE_PIERNAS_GLUTEOS, GymConstants.MUSCLE_PIERNAS, equipment = "${GymConstants.EQ_MANCUERNAS}/${GymConstants.EQ_PESO_CORPORAL}"),

            // PANTORRILLAS
            ExerciseEntity("67", "Elevación de Pantorrillas de Pie", "Pantorrillas", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("68", "Elevación de Pantorrillas Sentado", "Sóleo", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("69", "Elevación de Pantorrillas en Prensa", "Pantorrillas", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("70", "Burpees", GymConstants.MUSCLE_FULL_BODY, GymConstants.MUSCLE_CARDIO, equipment = GymConstants.EQ_PESO_CORPORAL),

            // ABDOMEN / CORE
            ExerciseEntity("71", "Plancha (Plank)", GymConstants.MUSCLE_CORE, GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("72", "Plancha Lateral", "Oblicuos", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("73", "Crunch Abdominal en Suelo", "Recto Abdominal", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("74", "Crunch en Máquina", "Recto Abdominal", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_MAQUINA),
            ExerciseEntity("75", "Elevaciones de Piernas Colgado", "Abdominales Inferiores", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("76", "Elevaciones de Piernas en Suelo", "Abdominales Inferiores", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),
            ExerciseEntity("77", "Giros Rusos con Peso", "Oblicuos", GymConstants.MUSCLE_ABDOMEN, equipment = "${GymConstants.EQ_MANCUERNAS}/Disco"),
            ExerciseEntity("78", "Rueda Abdominal", GymConstants.MUSCLE_CORE, GymConstants.MUSCLE_ABDOMEN, equipment = "Rueda"),
            ExerciseEntity("79", "Mountain Climbers", "${GymConstants.MUSCLE_CORE}/${GymConstants.MUSCLE_CARDIO}", GymConstants.MUSCLE_ABDOMEN, equipment = GymConstants.EQ_PESO_CORPORAL),

            // FUNCIONAL / OTROS
            ExerciseEntity("80", "Kettlebell Swing", GymConstants.MUSCLE_FULL_BODY, GymConstants.MUSCLE_CARDIO, equipment = "Kettlebell"),
            ExerciseEntity("81", "Peso Muerto Sumo", "${GymConstants.MUSCLE_PIERNAS}/${GymConstants.MUSCLE_ESPALDA}", GymConstants.MUSCLE_PIERNAS, equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("82", "Snatch con Mancuerna", GymConstants.MUSCLE_FULL_BODY, "Olímpico", equipment = GymConstants.EQ_MANCUERNAS),
            ExerciseEntity("83", "Clean and Press", GymConstants.MUSCLE_FULL_BODY, "Olímpico", equipment = GymConstants.EQ_BARRA),
            ExerciseEntity("84", "Box Jumps", GymConstants.MUSCLE_PIERNAS, "Pliométrico", equipment = "Cajón"),
            ExerciseEntity("85", "Saltos a la Comba", GymConstants.MUSCLE_FULL_BODY, GymConstants.MUSCLE_CARDIO, equipment = "Comba"),
            ExerciseEntity("86", "Battle Ropes", GymConstants.MUSCLE_FULL_BODY, GymConstants.MUSCLE_CARDIO, equipment = "Cuerdas"),
            ExerciseEntity("87", "Remo en Máquina (Ergómetro)", GymConstants.MUSCLE_FULL_BODY, GymConstants.MUSCLE_CARDIO, equipment = GymConstants.EQ_MAQUINA),
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