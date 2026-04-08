package edu.ucne.atlaspath.presentation.liveworkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkoutScreen(
    viewModel: LiveWorkoutViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.entrenamientoFinalizado) {
        if (state.entrenamientoFinalizado) onFinish()
    }

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
                        onClick = { viewModel.onEvent(LiveWorkoutEvent.FinalizarEntrenamiento) },
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(activeExercise.ejercicio.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text("Músculo: ${activeExercise.ejercicio.grupoMuscular}", style = MaterialTheme.typography.labelMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Set", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("LBS", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                                Text("Reps", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                                Text("✓", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            }

                            activeExercise.sets.forEachIndexed { setIndex, set ->
                                val isCompleted = set.isCompleted
                                val rowColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

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
                                        onValueChange = { viewModel.onEvent(LiveWorkoutEvent.UpdateSetValues(exIndex, setIndex, it, set.reps)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        enabled = !isCompleted, // Se bloquea si está marcado
                                        modifier = Modifier.weight(2f).height(50.dp).padding(horizontal = 4.dp),
                                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                    )
                                    OutlinedTextField(
                                        value = set.reps,
                                        onValueChange = { viewModel.onEvent(LiveWorkoutEvent.UpdateSetValues(exIndex, setIndex, set.weightLbs, it)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        enabled = !isCompleted,
                                        modifier = Modifier.weight(2f).height(50.dp).padding(horizontal = 4.dp),
                                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                    )
                                    IconButton(
                                        onClick = { viewModel.onEvent(LiveWorkoutEvent.ToggleSetComplete(exIndex, setIndex)) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completar",
                                            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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