package edu.ucne.atlaspath.presentation.tareas.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: NutritionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionUiState())
    val state = _state.asStateFlow()

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadUserProfile()
        loadDailyRecords()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userPreferences.userProfileFlow.collect { profile ->
                val weight = profile.weightLbs
                val metaCalculada = if (weight > 0f) (weight * 14).toInt() else 2000
                _state.update { it.copy(dailyCalorieGoal = metaCalculada) }
            }
        }
    }

    fun onEvent(event: NutritionEvent) {
        when (event) {
            is NutritionEvent.OnFoodInputChanged -> _state.update { it.copy(foodInputText = event.text, error = null) }
            NutritionEvent.AnalyzeAndSaveFood -> analyzeFood()
            is NutritionEvent.DeleteRecord -> deleteRecord(event.id)
            is NutritionEvent.OnRecipeInputChanged -> _state.update { it.copy(recipeInputText = event.text, error = null) }
            NutritionEvent.GenerateRecipe -> generateRecipe()
        }
    }

    private fun loadDailyRecords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getDailyRecords(todayString).collect { records ->
                val calories = records.sumOf { it.calorias }
                val protein = records.map { it.proteina }.sum()
                val carbs = records.map { it.carbohidratos }.sum()
                val fat = records.map { it.grasa }.sum()

                _state.update {
                    it.copy(
                        isLoading = false,
                        dailyRecords = records,
                        totalCalories = calories,
                        totalProtein = protein,
                        totalCarbs = carbs,
                        totalFat = fat
                    )
                }
            }
        }
    }

    private fun analyzeFood() {
        val foodText = _state.value.foodInputText
        if (foodText.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            when (val result = repository.analyzeAndSaveFood(foodText)) {
                is Resource.Success -> _state.update { it.copy(isSaving = false, foodInputText = "", isSaved = true) }
                is Resource.Error -> _state.update { it.copy(isSaving = false, error = result.message) }
                is Resource.Loading -> { }
            }
        }
    }

    private fun deleteRecord(id: Int) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }

    private fun generateRecipe() {
        val recipeText = _state.value.recipeInputText
        if (recipeText.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingRecipe = true, error = null) }
            when (val result = repository.generateRecipe(recipeText)) {
                is Resource.Success -> _state.update { it.copy(isGeneratingRecipe = false, generatedRecipe = result.data) }
                is Resource.Error -> _state.update { it.copy(isGeneratingRecipe = false, error = result.message) }
                is Resource.Loading -> { }
            }
        }
    }
}