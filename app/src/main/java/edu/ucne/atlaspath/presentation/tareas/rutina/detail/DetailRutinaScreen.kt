@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.rutina.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost
import kotlinx.coroutines.launch

@Composable
fun DetailRutinaScreen(
    viewModel: DetailRutinaViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = LocalSnackbarHost.current

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHost.showSnackbar("✅ Rutina Forjada con Éxito")
            onBack()
        }
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
    val scope = rememberCoroutineScope()

    val resultadosFiltrados = state.searchResults.filter { apiExercise ->
        state.ejercicios.none { it.nombre.equals(apiExercise.name, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.titulo.isBlank()) "Forjador de Rutinas" else state.titulo, fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } },
                actions = {
                    if (state.titulo.isNotBlank() && state.ejercicios.isNotEmpty()) {
                        IconButton(onClick = { shareRutinaAsText(context, state.titulo, state.ejercicios) }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onEvent(DetailRutinaEvent.SaveRutina) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = state.titulo.isNotBlank() && state.ejercicios.isNotEmpty() && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Armería", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.titulo,
                    onValueChange = { onEvent(DetailRutinaEvent.OnTituloChange(it)) },
                    placeholder = { Text("Nombre de la Rutina (Ej. Día de Piernas)", fontWeight = FontWeight.Medium) },
                    isError = state.tituloError != null,
                    supportingText = { if (state.tituloError != null) Text(state.tituloError!!, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            }

            item {
                Button(
                    onClick = onNavigateToAi,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Forjar con Inteligencia Artificial ✨", fontWeight = FontWeight.Black)
                }
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Añadir Ejercicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(DetailRutinaEvent.OnSearchQueryChange(it)) },
                    placeholder = { Text("Buscar ejercicio o músculo...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { if (state.isSearching) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            if (state.searchQuery.isBlank() && resultadosFiltrados.isEmpty()) {
                item {
                    Text("Sugerencias Rápidas", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val populares = listOf(
                            "Press de Banca" to "Pecho", "Sentadilla" to "Piernas",
                            "Peso Muerto" to "Espalda", "Dominadas" to "Espalda",
                            "Curl de Bíceps" to "Brazos", "Press Militar" to "Hombros",
                            "Plancha" to "Core"
                        )
                        populares.forEach { (nombre, musculo) ->
                            Surface(
                                modifier = Modifier.clickable { onEvent(DetailRutinaEvent.AddEjercicioManual(nombre, musculo)) },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                            ) {
                                Text("+ $nombre", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }

            if (resultadosFiltrados.isNotEmpty()) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            resultadosFiltrados.forEachIndexed { index, localExercise ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        onEvent(DetailRutinaEvent.AddEjercicio(exerciseDto = localExercise, series = 4, reps = 10, descanso = 60))
                                    }.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(localExercise.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Músculo: ${localExercise.target}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = "Añadir", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                    }
                                }
                                if (index < resultadosFiltrados.lastIndex) {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            if (state.ejercicios.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tu Selección", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(state.ejercicios) { index, ejercicio ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ejercicio.nombre, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${ejercicio.grupoMuscular} • ${ejercicio.series} x ${ejercicio.repeticiones} reps",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onEvent(DetailRutinaEvent.RemoveEjercicio(index)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
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

@Preview(showBackground = true)
@Composable
fun DetailRutinaEmptyPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
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
}

@Preview(showBackground = true)
@Composable
fun DetailRutinaSearchingPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                val mockResults = listOf(
                    ExerciseDto(id = "1", name = "Press de Banca con Barra", bodyPart = "Pecho", target = "Pecho", equipment = "Barra", gifUrl = null, instructions = null),
                    ExerciseDto(id = "2", name = "Aperturas con Mancuernas", bodyPart = "Pecho", target = "Pecho", equipment = "Mancuernas", gifUrl = null, instructions = null)
                )

                DetailRutinaBodyScreen(
                    state = DetailRutinaUiState(
                        titulo = "Día de Empuje",
                        searchQuery = "Pecho",
                        searchResults = mockResults
                    ),
                    onEvent = {},
                    onBack = {},
                    onNavigateToAi = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRutinaFilledPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                val mockEjercicios = listOf(
                    Ejercicio(nombre = "Press de Banca con Barra", series = 4, repeticiones = 10, descansoSegundos = 90, grupoMuscular = "Pecho"),
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
}