@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")
package edu.ucne.atlaspath.presentation.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToGenerador: () -> Unit,
    onNavigateToBiblioteca: () -> Unit,
    onNavigateToRutina: (Int) -> Unit,
    onCreateRutina: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToGenerador = onNavigateToGenerador,
        onNavigateToBiblioteca = onNavigateToBiblioteca,
        onNavigateToRutina = onNavigateToRutina,
        onCreateRutina = onCreateRutina,
        onNavigateToHistorial = onNavigateToHistorial,
        onNavigateToCalendar = onNavigateToCalendar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBodyScreen(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToGenerador: () -> Unit,
    onNavigateToBiblioteca: () -> Unit,
    onNavigateToRutina: (Int) -> Unit,
    onCreateRutina: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToCalendar: () -> Unit // <-- NUEVO PARÁMETRO
) {
    var showAthleteCard by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    if (showAthleteCard) {
        AthleteCardDialog(
            state = state,
            onDismiss = { showAthleteCard = false },
            onEditProfileClick = {
                showAthleteCard = false
                showEditProfileDialog = true
            }
        )
    }

    if (showWeightDialog) {
        UpdateWeightDialog(
            currentWeight = state.pesoActualLbs,
            onDismiss = { showWeightDialog = false },
            onSave = { nuevoPeso ->
                onEvent(DashboardEvent.UpdatePeso(nuevoPeso))
                showWeightDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(state.userName) }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre de Atleta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) {
                        onEvent(DashboardEvent.UpdateName(newName))
                    }
                    showEditProfileDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onNavigateToHistorial) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Ver Historial",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { showAthleteCard = true }, // Click abre el perfil
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.userName.ifBlank { "Atleta" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "\"En camino a la grandeza\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Nvl. ${state.nivelActual}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progreso al Nivel ${state.nivelActual + 1}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("${(state.progresoNivel * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { state.progresoNivel },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { onNavigateToCalendar() } // <-- AHORA ES CLICKEABLE
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Esta Semana", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${state.totalEntrenamientos} Entrenos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")

                        val diasEntrenados = state.diasEntrenadosSemana

                        diasSemana.forEachIndexed { index, dia ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (diasEntrenados.getOrElse(index) { false }) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (diasEntrenados.getOrElse(index) { false }) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(dia, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(dia, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .clickable { showWeightDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Peso Corporal Actual", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Row(verticalAlignment = Alignment.Bottom) {
                            val pesoFormateado = if (state.pesoActualLbs > 0) String.format(java.util.Locale.US, "%.1f", state.pesoActualLbs) else "--"
                            Text(
                                text = pesoFormateado,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("lbs", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Actualizar Peso",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }

            item {
                Text("Rangos por Grupo Muscular", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.rangosMusculares.isEmpty()) {
                        Text(
                            text = "Aún no tienes medallas. ¡Entrena para medir tu fuerza!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.rangosMusculares.forEach { rango ->
                            MuscleRankRow(rango.musculo, rango.rangoNombre, rango.progreso, rango.medalla)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Entrenamiento Sugerido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onCreateRutina) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crear Rutina", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.rutinaHoy != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(state.rutinaHoy.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            if (state.rutinaHoy.descripcion.isNotBlank()) {
                                Text(state.rutinaHoy.descripcion, style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateToRutina(state.rutinaHoy.rutinaId ?: 0) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.FitnessCenter, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Comenzar Ahora", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = onNavigateToBiblioteca,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Elegir otra rutina de la Biblioteca")
                            }
                        }
                    }
                } else {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCreateRutina() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("¡Tu armería está vacía!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Crea tu primera rutina de combate o deja que la IA la forje por ti.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onCreateRutina() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Crear mi primera rutina", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun UpdateWeightDialog(
    currentWeight: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var weightValue by remember { mutableFloatStateOf(if (currentWeight > 0) currentWeight.toFloat() else 150f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Peso", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Desliza para registrar tu peso actual.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "${weightValue.toInt()} lbs", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Slider(value = weightValue, onValueChange = { weightValue = it }, valueRange = 90f..400f, steps = 310)
            }
        },
        confirmButton = { Button(onClick = { onSave(weightValue.toDouble()) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun MuscleRankRow(musculo: String, rango: String, progress: Float, medalla: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(musculo, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(medalla, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(rango, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    }
}

@Composable
fun AthleteCardDialog(
    state: DashboardUiState,
    onDismiss: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().capturable(captureController),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface))).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = onEditProfileClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Perfil", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).border(4.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = state.userName.ifBlank { "Atleta" }.uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text(text = "ATLASPATH WARRIOR", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CardStatItem("NIVEL", "${state.nivelActual}")
                    CardStatItem("ENTRENOS", "${state.totalEntrenamientos}")
                    CardStatItem("VOLUMEN", "${(state.volumenTotalLbs / 1000).toInt()}k")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                                shareBitmapAsImage(context, bitmap, state.userName, state.nivelActual)
                                onDismiss()
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir Logros", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CardStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun shareBitmapAsImage(context: Context, bitmap: Bitmap, userName: String, userLevel: Int) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/${userName}_AtlasCard.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val newFile = File(cachePath, "${userName}_AtlasCard.png")
        val contentUri = FileProvider.getUriForFile(context, "edu.ucne.atlaspath.fileprovider", newFile)

        if (contentUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "¡Mira mi progreso en AtlasPath! Soy nivel $userLevel. #gym #rpg #AtlasPath")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Compartir mi Tarjeta Atlas"))
        }
    } catch (e: IOException) { e.printStackTrace() }
}