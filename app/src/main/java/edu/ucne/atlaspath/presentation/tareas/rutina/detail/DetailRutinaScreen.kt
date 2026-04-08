@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package edu.ucne.atlaspath.presentation.tareas.rutina.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import edu.ucne.atlaspath.domain.model.Ejercicio

@Composable
fun DetailRutinaScreen(
    viewModel: DetailRutinaViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) onBack()
    }

    DetailRutinaBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onNavigateToAi = onNavigateToAi
    )
}

@Composable
fun DetailRutinaBodyScreen(
    state: DetailRutinaUiState,
    onEvent: (DetailRutinaEvent) -> Unit,
    onBack: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val context = LocalContext.current

    val resultadosFiltrados = state.searchResults.filter { apiExercise ->
        state.ejercicios.none { it.nombre.equals(apiExercise.name, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.titulo.isBlank()) "Nueva Rutina" else state.titulo, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } },
                actions = {
                    if (state.titulo.isNotBlank() && state.ejercicios.isNotEmpty()) {
                        IconButton(onClick = { shareRutinaAsText(context, state.titulo, state.ejercicios) }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(
                        onClick = { onEvent(DetailRutinaEvent.SaveRutina) },
                        enabled = state.titulo.isNotBlank() && state.ejercicios.isNotEmpty() && !state.isLoading
                    ) {
                        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Save, contentDescription = "Guardar", tint = if (state.titulo.isNotBlank() && state.ejercicios.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.titulo,
                    onValueChange = { onEvent(DetailRutinaEvent.OnTituloChange(it)) },
                    label = { Text("Nombre de la Rutina (Ej. Pecho Intenso) *") },
                    isError = state.tituloError != null,
                    supportingText = { if (state.tituloError != null) Text(state.tituloError!!) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                Text("Añadir Ejercicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(DetailRutinaEvent.OnSearchQueryChange(it)) },
                    placeholder = { Text("Buscar en la API (ej. Press)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { if (state.isSearching) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (state.searchQuery.isBlank() && resultadosFiltrados.isEmpty()) {
                item {
                    Text("Sugerencias Rápidas", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val populares = listOf(
                            "Press de Banca" to "Pecho", "Sentadilla" to "Piernas",
                            "Peso Muerto" to "Espalda", "Dominadas" to "Espalda",
                            "Curl de Bíceps" to "Brazos", "Press Militar" to "Hombros",
                            "Abdominales" to "Core"
                        )
                        populares.forEach { (nombre, musculo) ->
                            Surface(
                                modifier = Modifier.clickable { onEvent(DetailRutinaEvent.AddEjercicioManual(nombre, musculo)) },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text("+ $nombre", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            if (resultadosFiltrados.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column {
                            resultadosFiltrados.forEach { apiExercise ->
                                ListItem(
                                    headlineContent = { Text(apiExercise.name.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text("Músculo: ${apiExercise.target?.replaceFirstChar { it.uppercase() } ?: "General"}") },
                                    trailingContent = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                                    modifier = Modifier.clickable {
                                        onEvent(DetailRutinaEvent.AddEjercicio(
                                            exerciseDto = apiExercise, series = 4, reps = 10, descanso = 60
                                        ))
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateToAi,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dejar que la IA la cree ✨", fontWeight = FontWeight.Bold)
                }
            }

            if (state.ejercicios.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Tu Selección:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                itemsIndexed(state.ejercicios) { index, ejercicio ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ejercicio.nombre, fontWeight = FontWeight.Bold)
                                Text("${ejercicio.grupoMuscular} | ${ejercicio.series} x ${ejercicio.repeticiones} reps", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onEvent(DetailRutinaEvent.RemoveEjercicio(index)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

fun shareRutinaAsText(context: Context, titulo: String, ejercicios: List<Ejercicio>) {
    var shareText = "🔥 *Rutina: $titulo* 🔥\n\n"
    ejercicios.forEachIndexed { index, ej -> shareText += "🔸 ${index + 1}. ${ej.nombre}\n   └ ${ej.series} series x ${ej.repeticiones} reps\n" }
    shareText += "\n🚀 Creado con *AtlasPath*"
    val sendIntent = Intent().apply { action = Intent.ACTION_SEND; putExtra(Intent.EXTRA_TEXT, shareText); type = "text/plain" }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir Rutina AtlasPath"))
}

// --- PREVIEWS (Obligatorio Material 3 y UDF) ---

@Preview(showBackground = true)
@Composable
fun DetailRutinaEmptyPreview() {
    MaterialTheme {
        Surface {
            DetailRutinaBodyScreen(
                state = DetailRutinaUiState(
                    titulo = "",
                    searchQuery = ""
                ),
                onEvent = {},
                onBack = {},
                onNavigateToAi = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRutinaSearchingPreview() {
    MaterialTheme {
        Surface {
            val mockResults = listOf(
                ExerciseDto(id = "1", name = "Barbell Bench Press", bodyPart = "chest", target = "Pecho", equipment = "barbell", gifUrl = null, instructions = null),
                ExerciseDto(id = "2", name = "Dumbbell Bench Press", bodyPart = "chest", target = "Pecho", equipment = "dumbbell", gifUrl = null, instructions = null)
            )

            DetailRutinaBodyScreen(
                state = DetailRutinaUiState(
                    titulo = "Día de Empuje",
                    searchQuery = "Press",
                    searchResults = mockResults
                ),
                onEvent = {},
                onBack = {},
                onNavigateToAi = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRutinaFilledPreview() {
    MaterialTheme {
        Surface {
            val mockEjercicios = listOf(
                Ejercicio(nombre = "Press de Banca", series = 4, repeticiones = 10, descansoSegundos = 90, grupoMuscular = "Pecho"),
                Ejercicio(nombre = "Aperturas con Mancuernas", series = 3, repeticiones = 12, descansoSegundos = 60, grupoMuscular = "Pecho")
            )

            DetailRutinaBodyScreen(
                state = DetailRutinaUiState(
                    titulo = "Pecho Intenso",
                    ejercicios = mockEjercicios
                ),
                onEvent = {},
                onBack = {},
                onNavigateToAi = {}
            )
        }
    }
}