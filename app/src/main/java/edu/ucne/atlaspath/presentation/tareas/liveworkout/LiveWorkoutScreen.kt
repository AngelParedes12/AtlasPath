@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.liveworkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost

@Composable
fun LiveWorkoutScreen(
    viewModel: LiveWorkoutViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = LocalSnackbarHost.current

    LaunchedEffect(state.entrenamientoFinalizado) {
        if (state.entrenamientoFinalizado) {
            val setsCompletados = state.activeExercises.flatMap { it.sets }.count { it.isCompleted }
            if (setsCompletados > 0) {
                snackbarHost.showSnackbar("⚔️ ¡Entrenamiento finalizado! Ganaste experiencia.")
            } else {
                snackbarHost.showSnackbar("⚠️ Entrenamiento descartado (sin actividad).")
            }
            onFinish()
        }
    }

    LiveWorkoutBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onCancel = onCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkoutBodyScreen(
    state: LiveWorkoutUiState,
    onEvent: (LiveWorkoutEvent) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape) {
                        Text(
                            text = formatTime(state.cronometroTotal),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) { Icon(Icons.Default.Close, "Cancelar") }
                },
                actions = {
                    Button(
                        onClick = { onEvent(LiveWorkoutEvent.FinalizarEntrenamiento) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Terminar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = state.rutinaTitulo.ifBlank { "Entrenamiento Libre" }.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(state.activeExercises) { exIndex, activeExercise ->
                    val allSetsCompleted = activeExercise.sets.isNotEmpty() && activeExercise.sets.all { it.isCompleted }
                    var isExpanded by rememberSaveable { mutableStateOf(true) }

                    LaunchedEffect(allSetsCompleted) {
                        if (allSetsCompleted) {
                            isExpanded = false
                        }
                    }

                    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "rotation")

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (allSetsCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (allSetsCompleted) 0.dp else 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpanded = !isExpanded }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeExercise.ejercicio.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = if (allSetsCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary
                                        )
                                        if (allSetsCompleted) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Completado",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (allSetsCompleted) "¡Ejercicio Completado!" else "Músculo: ${activeExercise.ejercicio.grupoMuscular}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (allSetsCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    modifier = Modifier.rotate(rotationState),
                                    tint = if (allSetsCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Set", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                        Text("LBS", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                                        Text("Reps", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                                        Text("✓", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    }

                                    activeExercise.sets.forEachIndexed { setIndex, set ->
                                        val isCompleted = set.isCompleted
                                        val rowColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                        val isValidReps = set.reps.isNotBlank() && (set.reps.toIntOrNull() ?: 0) > 0

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(rowColor)
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("${set.setId}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                            OutlinedTextField(
                                                value = set.weightLbs,
                                                onValueChange = { onEvent(LiveWorkoutEvent.UpdateSetValues(exIndex, setIndex, it, set.reps)) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                enabled = !isCompleted,
                                                modifier = Modifier.weight(2f).height(50.dp).padding(horizontal = 4.dp),
                                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                                    disabledBorderColor = Color.Transparent,
                                                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            OutlinedTextField(
                                                value = set.reps,
                                                onValueChange = { onEvent(LiveWorkoutEvent.UpdateSetValues(exIndex, setIndex, set.weightLbs, it)) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                enabled = !isCompleted,
                                                modifier = Modifier.weight(2f).height(50.dp).padding(horizontal = 4.dp),
                                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                                    disabledBorderColor = Color.Transparent,
                                                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            IconButton(
                                                onClick = { onEvent(LiveWorkoutEvent.ToggleSetComplete(exIndex, setIndex)) },
                                                enabled = isValidReps || isCompleted,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Completar",
                                                    tint = if (isCompleted) MaterialTheme.colorScheme.primary else if (isValidReps) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        TextButton(
                                            onClick = { onEvent(LiveWorkoutEvent.RemoveSet(exIndex)) },
                                            enabled = activeExercise.sets.isNotEmpty()
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Quitar", fontWeight = FontWeight.Bold)
                                        }
                                        TextButton(onClick = { onEvent(LiveWorkoutEvent.AddSet(exIndex)) }) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Añadir Set", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiveWorkoutPreview() {
    MaterialTheme {
        Surface {
            val mockSets = listOf(
                ActiveSet(setId = 1, weightLbs = "135", reps = "10", isCompleted = true),
                ActiveSet(setId = 2, weightLbs = "135", reps = "8", isCompleted = false),
                ActiveSet(setId = 3, weightLbs = "", reps = "", isCompleted = false)
            )

            val mockExercises = listOf(
                ActiveExercise(
                    ejercicio = Ejercicio(nombre = "Press de Banca", series = 3, repeticiones = 10, descansoSegundos = 60, grupoMuscular = "Pecho"),
                    sets = mockSets
                )
            )

            LiveWorkoutBodyScreen(
                state = LiveWorkoutUiState(
                    isLoading = false,
                    cronometroTotal = 125,
                    rutinaTitulo = "Día de Pecho",
                    activeExercises = mockExercises
                ),
                onEvent = {},
                onCancel = {}
            )
        }
    }
}