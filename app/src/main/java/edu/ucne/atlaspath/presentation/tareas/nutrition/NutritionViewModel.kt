package edu.ucne.atlaspath.presentation.tareas.nutrition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.repository.NutritionRepository
import edu.ucne.atlaspath.presentation.tareas.navigation.Screen
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Screen.Nutrition>()
    private val metaCalculada = (args.pesoLbs * 14).toInt()

    private val _state = MutableStateFlow(NutritionUiState(dailyCalorieGoal = metaCalculada))
    val state = _state.asStateFlow()

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadDailyRecords()
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
            when (val result = repository.analyzeAndSaveFood(foodText)) {
                is Resource.Success -> _state.update { it.copy(isSaving = false, foodInputText = "") }
                is Resource.Error -> _state.update { it.copy(isSaving = false, error = result.message) }
                is Resource.Loading -> _state.update { it.copy(isSaving = true, error = null) }
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
            when (val result = repository.generateRecipe(recipeText)) {
                is Resource.Success -> _state.update { it.copy(isGeneratingRecipe = false, generatedRecipe = result.data) }
                is Resource.Error -> _state.update { it.copy(isGeneratingRecipe = false, error = result.message) }
                is Resource.Loading -> _state.update { it.copy(isGeneratingRecipe = true, error = null) }
            }
        }
    }
}