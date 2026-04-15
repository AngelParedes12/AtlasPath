@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt

@Composable
fun PhysicalProfileScreen(
    viewModel: PhysicalProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProfileSaved: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PhysicalProfileBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onProfileSaved = onProfileSaved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalProfileBodyScreen(
    state: PhysicalProfileUiState,
    onEvent: (PhysicalProfileEvent) -> Unit,
    onBack: () -> Unit,
    onProfileSaved: () -> Unit
) {
    val scrollState = rememberScrollState()

    if (state.showSomatotypeHelp) {
        SomatotypeHelpDialog(onDismiss = { onEvent(PhysicalProfileEvent.ToggleHelp(false)) })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil Atleta", fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
                AnimatedVisibility(visible = state.showValidationError) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Por favor, completa todos los campos del perfil.",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Button(
                    onClick = { onEvent(PhysicalProfileEvent.SaveProfile(onProfileSaved)) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Finalizar Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ayuda a Atlas a conocerte", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

            ProfileDropdown("Género Biológico", state.selectedGender, listOf("Hombre", "Mujer", "Otro")) { onEvent(PhysicalProfileEvent.OnGenderChange(it)) }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileSlider("Edad", "${state.age.roundToInt()} años", state.age, 14f..90f) { onEvent(PhysicalProfileEvent.OnAgeChange(it)) }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileUnitSlider("Peso Corporal", if (state.isKg) "kg" else "lbs", state.weightValue, if (state.isKg) 40f..160f else 90f..350f, { onEvent(PhysicalProfileEvent.OnWeightChange(it)) }) {
                        onEvent(PhysicalProfileEvent.ToggleWeightUnit)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileUnitSlider("Estatura", if (state.isCm) "cm" else "ft", state.heightValue, if (state.isCm) 140f..210f else 4.5f..7f, { onEvent(PhysicalProfileEvent.OnHeightChange(it)) }) {
                        onEvent(PhysicalProfileEvent.ToggleHeightUnit)
                    }
                }
            }

            ProfileDropdown("Experiencia", state.selectedLevel, listOf("Principiante", "Intermedio", "Avanzado")) { onEvent(PhysicalProfileEvent.OnLevelChange(it)) }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tipo de Cuerpo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    IconButton(
                        onClick = { onEvent(PhysicalProfileEvent.ToggleHelp(true)) },
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDropdown("", state.selectedSomatotype, listOf("Ectomorfo", "Mesomorfo", "Endomorfo")) { onEvent(PhysicalProfileEvent.OnSomatotypeChange(it)) }
            }

            Column {
                Text("Objetivo principal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDropdown("", state.selectedGoal, listOf("Pérdida de Grasa", "Ganancia Muscular", "Recomposición", "Mantenimiento")) { onEvent(PhysicalProfileEvent.OnGoalChange(it)) }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SomatotypeHelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Tipos de Cuerpo", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row { Text("💪", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp)); Column { Text("Ectomorfo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Delgado, extremidades largas. Dificultad para ganar peso.", style = MaterialTheme.typography.bodyMedium) } }
                Row { Text("🏋️", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp)); Column { Text("Mesomorfo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Atlético natural. Facilidad para ganar músculo.", style = MaterialTheme.typography.bodyMedium) } }
                Row { Text("🐻", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp)); Column { Text("Endomorfo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Ancho, metabolismo lento. Facilidad para ganar volumen.", style = MaterialTheme.typography.bodyMedium) } }
            }
        },
        confirmButton = { Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) { Text("Entendido", fontWeight = FontWeight.Bold) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { if (label.isNotBlank()) Text(label, fontWeight = FontWeight.SemiBold) },
            placeholder = { Text("Seleccionar...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            options.forEach { opt -> DropdownMenuItem(text = { Text(opt, fontWeight = FontWeight.Medium) }, onClick = { onSelect(opt); expanded = false }) }
        }
    }
}

@Composable
fun ProfileSlider(label: String, valueText: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valueText, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = range, colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant))
    }
}

@Composable
fun ProfileUnitSlider(label: String, unit: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit, onUnitToggle: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(modifier = Modifier.clickable { onUnitToggle() }, shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                Text("Cambiar a ${if (unit == "kg") "Lbs" else if (unit == "cm") "Ft" else if (unit == "lbs") "Kg" else "Cm"}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("${if (unit == "ft") String.format("%.1f", value) else value.roundToInt()} $unit", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = range, colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhysicalProfilePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            PhysicalProfileBodyScreen(
                state = PhysicalProfileUiState(),
                onEvent = {},
                onBack = {},
                onProfileSaved = {}
            )
        }
    }
}