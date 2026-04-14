@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.rutina.list

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost
import kotlinx.coroutines.launch

@Composable
fun ListRutinaScreen(
    viewModel: ListRutinaViewModel = hiltViewModel(),
    onRutinaClick: (Int) -> Unit,
    onCreateRutina: () -> Unit,
    onTrainClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListRutinaBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onRutinaClick = onRutinaClick,
        onCreateRutina = onCreateRutina,
        onTrainClick = onTrainClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListRutinaBodyScreen(
    state: ListRutinaUiState,
    onEvent: (ListRutinaEvent) -> Unit,
    onRutinaClick: (Int) -> Unit,
    onCreateRutina: () -> Unit,
    onTrainClick: (Int) -> Unit
) {
    val context = LocalContext.current
    var rutinaToDelete by remember { mutableStateOf<Rutina?>(null) }

    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()

    if (rutinaToDelete != null) {
        AlertDialog(
            onDismissRequest = { rutinaToDelete = null },
            title = { Text("¿Eliminar ${rutinaToDelete?.titulo}?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro de querer borrar esta rutina de tu biblioteca?") },
            confirmButton = {
                Button(
                    onClick = {
                        rutinaToDelete?.rutinaId?.let { id ->
                            onEvent(ListRutinaEvent.DeleteRutina(id))
                            scope.launch { snackbarHost.showSnackbar("🗑️ Rutina eliminada de tu biblioteca") }
                        }
                        rutinaToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { rutinaToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Biblioteca", fontWeight = FontWeight.Black) }
            )
        },
        floatingActionButton = {
            if (state.rutinas.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onCreateRutina,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Rutina", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.rutinas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Tu biblioteca está vacía", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Forja tu primera rutina de combate o deja que la IA la diseñe por ti.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onCreateRutina,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Forjar mi primera rutina", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Tus Planes de Entrenamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(state.rutinas) { rutina ->
                        val musculosTrabajados = rutina.ejercicios.map { it.grupoMuscular }.distinct().take(3).joinToString(", ")

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(rutina.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                        if (rutina.descripcion.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(rutina.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = CircleShape
                                        ) {
                                            Text("${rutina.ejercicios.size} Ejercicios", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { shareRutinaFromListAsText(context, rutina.titulo, rutina.ejercicios) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Compartir", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(
                                            onClick = { rutinaToDelete = rutina },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Borrar", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (musculosTrabajados.isNotBlank()) "Enfocado en: $musculosTrabajados" else "Cuerpo Completo",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onRutinaClick(rutina.rutinaId) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Editar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onTrainClick(rutina.rutinaId) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Entrenar", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Entrenar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun shareRutinaFromListAsText(context: Context, titulo: String, ejercicios: List<Ejercicio>) {
    var shareText = "🔥 *Rutina: $titulo* 🔥\n\n"
    ejercicios.forEachIndexed { index, ej -> shareText += "🔸 ${index + 1}. ${ej.nombre}\n   └ ${ej.series} series x ${ej.repeticiones} reps\n" }
    shareText += "\n🚀 Creado con *AtlasPath*"
    val sendIntent = Intent().apply { action = Intent.ACTION_SEND; putExtra(Intent.EXTRA_TEXT, shareText); type = "text/plain" }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir Rutina AtlasPath"))
}

@Preview(showBackground = true)
@Composable
fun ListRutinaEmptyPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                ListRutinaBodyScreen(
                    state = ListRutinaUiState(isLoading = false, rutinas = emptyList()),
                    onEvent = {},
                    onRutinaClick = {},
                    onCreateRutina = {},
                    onTrainClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListRutinaPopulatedPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                val mockRutinas = listOf(
                    Rutina(
                        rutinaId = 1,
                        titulo = "Fuerza Espartana",
                        descripcion = "Día pesado para romper marcas.",
                        ejercicios = listOf(
                            Ejercicio(nombre = "Press de Banca", series = 4, repeticiones = 8, descansoSegundos = 90, grupoMuscular = "Pecho"),
                            Ejercicio(nombre = "Sentadilla", series = 4, repeticiones = 8, descansoSegundos = 90, grupoMuscular = "Piernas")
                        )
                    ),
                    Rutina(
                        rutinaId = 2,
                        titulo = "Brazos de Acero",
                        descripcion = "",
                        ejercicios = listOf(
                            Ejercicio(nombre = "Curl de Bíceps", series = 3, repeticiones = 12, descansoSegundos = 60, grupoMuscular = "Brazos")
                        )
                    )
                )

                ListRutinaBodyScreen(
                    state = ListRutinaUiState(isLoading = false, rutinas = mockRutinas),
                    onEvent = {},
                    onRutinaClick = {},
                    onCreateRutina = {},
                    onTrainClick = {}
                )
            }
        }
    }
}