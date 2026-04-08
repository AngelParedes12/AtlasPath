package edu.ucne.atlaspath.presentation.tareas.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalProfileScreen(
    onBack: () -> Unit,
    onProfileSaved: (Int, Float, Float, String, String, String, String) -> Unit
) {
    val scrollState = rememberScrollState()
    var showValidationError by remember { mutableStateOf(false) }
    var age by remember { mutableFloatStateOf(25f) }
    var isKg by remember { mutableStateOf(true) }
    var weightValue by remember { mutableFloatStateOf(70f) }
    var isCm by remember { mutableStateOf(true) }
    var heightValue by remember { mutableFloatStateOf(170f) }
    var selectedGender by remember { mutableStateOf("Seleccionar...") }
    var selectedLevel by remember { mutableStateOf("Seleccionar...") }
    var selectedSomatotype by remember { mutableStateOf("Seleccionar...") }
    var selectedGoal by remember { mutableStateOf("Seleccionar...") }
    var showSomatotypeHelp by remember { mutableStateOf(false) }

    if (showSomatotypeHelp) {
        AlertDialog(
            onDismissRequest = { showSomatotypeHelp = false },
            title = { Text("Tipos de Cuerpo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💪 **Ectomorfo:** Estructura ósea pequeña y extremidades largas. Generalmente con dificultad para ganar peso.", style = MaterialTheme.typography.bodyMedium)
                    Text("🏋️ **Mesomorfo:** Estructura ósea mediana y cuerpo atlético. Facilidad para ganar músculo.", style = MaterialTheme.typography.bodyMedium)
                    Text("🐻 **Endomorfo:** Estructura ósea grande y metabolismo más lento. Facilidad para ganar fuerza y volumen.", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSomatotypeHelp = false }) { Text("Entendido") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil Atleta", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (showValidationError) {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("Completa todos los campos", Modifier.padding(8.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                ExtendedFloatingActionButton(
                    onClick = {
                        val valid = selectedGender != "Seleccionar..." && selectedLevel != "Seleccionar..." && selectedSomatotype != "Seleccionar..." && selectedGoal != "Seleccionar..."
                        if (valid) {
                            val finalWeight = if (isKg) weightValue * 2.20462f else weightValue
                            val finalHeight = if (!isCm) heightValue * 30.48f else heightValue
                            onProfileSaved(age.roundToInt(), finalWeight, finalHeight, selectedSomatotype, selectedGoal, selectedLevel, selectedGender)
                        } else { showValidationError = true }
                    },
                    icon = { Icon(Icons.Filled.ArrowForward, null) },
                    text = { Text("Finalizar") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Ayuda a Atlas a conocerte", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            ProfileDropdown("Género Biológico", selectedGender, listOf("Hombre", "Mujer", "Otro")) { selectedGender = it }
            ProfileSlider("Edad", "${age.roundToInt()} años", age, 14f..90f) { age = it }
            ProfileUnitSlider("Peso", if (isKg) "kg" else "lbs", weightValue, if (isKg) 40f..160f else 90f..350f, { weightValue = it }) {
                isKg = !isKg
                weightValue = if (isKg) weightValue / 2.2f else weightValue * 2.2f
            }
            ProfileUnitSlider("Altura", if (isCm) "cm" else "ft", heightValue, if (isCm) 140f..210f else 4.5f..7f, { heightValue = it }) {
                isCm = !isCm
                heightValue = if (isCm) heightValue * 30.48f else heightValue / 30.48f
            }
            ProfileDropdown("Experiencia", selectedLevel, listOf("Principiante", "Intermedio", "Avanzado")) { selectedLevel = it }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tipo de Cuerpo", fontWeight = FontWeight.Bold)
                IconButton(onClick = { showSomatotypeHelp = true }) { Icon(Icons.Default.Info, null, modifier = Modifier.size(20.dp)) }
            }
            ProfileDropdown("", selectedSomatotype, listOf("Ectomorfo", "Mesomorfo", "Endomorfo")) { selectedSomatotype = it }
            Text("¿Cuál es tu objetivo principal?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            ProfileDropdown("", selectedGoal, listOf("Pérdida de Grasa", "Ganancia Muscular", "Recomposición", "Mantenimiento")) { selectedGoal = it }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- COMPONENTES REUTILIZABLES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { if (label.isNotBlank()) Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false }) }
        }
    }
}

@Composable
fun ProfileSlider(label: String, valueText: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text(label, fontWeight = FontWeight.Bold); Text(valueText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}

@Composable
fun ProfileUnitSlider(label: String, unit: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit, onUnitToggle: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold)
            TextButton(onClick = onUnitToggle) { Text("Cambiar a ${if (unit == "kg") "Lbs" else if (unit == "cm") "Ft" else if (unit == "lbs") "Kg" else "Cm"}") }
        }
        Text("${if (unit == "ft") String.format("%.1f", value) else value.roundToInt()} $unit", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}

// --- VIEWMODEL (Incluido aquí para evitar errores de referencia) ---
@HiltViewModel
class PhysicalProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun saveProfile(age: Int, weight: Float, height: Float, somatotype: String, goal: String, gymLevel: String, gender: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.savePhysicalProfile(age, weight, height, somatotype, goal, gymLevel, gender)
            userPreferences.saveOnboardingCompleted(true)
            onComplete()
        }
    }
}