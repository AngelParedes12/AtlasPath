package edu.ucne.atlaspath.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_WEIGHT_LBS = floatPreferencesKey("user_weight_lbs")
        val USER_HEIGHT_CM = floatPreferencesKey("user_height_cm")
        val USER_SOMATOTYPE = stringPreferencesKey("user_somatotype")
        val USER_GOAL = stringPreferencesKey("user_goal")
        val USER_GYM_LEVEL = stringPreferencesKey("user_gym_level")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: "" }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun savePhysicalProfile(
        age: Int,
        weightLbs: Float,
        heightCm: Float,
        somatotype: String,
        goal: String,
        gymLevel: String,
        gender: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USER_AGE] = age
            preferences[USER_WEIGHT_LBS] = weightLbs
            preferences[USER_HEIGHT_CM] = heightCm
            preferences[USER_SOMATOTYPE] = somatotype
            preferences[USER_GOAL] = goal
            preferences[USER_GYM_LEVEL] = gymLevel
            preferences[USER_GENDER] = gender
        }
    }

    suspend fun updateWeight(newWeightLbs: Float) {
        dataStore.edit { preferences ->
            preferences[USER_WEIGHT_LBS] = newWeightLbs
        }
    }

    val userProfileFlow: Flow<UserProfile> = dataStore.data.map { preferences ->
        UserProfile(
            age = preferences[USER_AGE] ?: 0,
            weightLbs = preferences[USER_WEIGHT_LBS] ?: 0f,
            heightCm = preferences[USER_HEIGHT_CM] ?: 0f,
            somatotype = preferences[USER_SOMATOTYPE] ?: "",
            goal = preferences[USER_GOAL] ?: "",
            gymLevel = preferences[USER_GYM_LEVEL] ?: "",
            gender = preferences[USER_GENDER] ?: "",
        )
    }
}

data class UserProfile(
    val age: Int,
    val weightLbs: Float,
    val heightCm: Float,
    val somatotype: String,
    val goal: String,
    val gymLevel: String,
    val gender: String = "",
)