@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.aicreator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiCreatorScreen(
    viewModel: AiCreatorViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToLiveWorkout: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = LocalSnackbarHost.current

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHost.showSnackbar("✨ Magia Forjada: Rutina guardada en tu biblioteca")
            onBack()
        }
    }

    AiCreatorBodyScreen(state, viewModel::onEvent, onBack)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiCreatorBodyScreen(
    state: AiCreatorUiState,
    onEvent: (AiCreatorEvent) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("IA Creador", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
        },
        bottomBar = {
            if (state.rutinaGenerada != null && !state.isLoading) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch { snackbarHost.showSnackbar("🗑️ Rutina descartada") }
                                onEvent(AiCreatorEvent.DiscardRoutine)
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descartar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Button(
                            onClick = { onEvent(AiCreatorEvent.SaveGeneratedRoutine) },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = when {
                    state.isLoading -> 1
                    state.rutinaGenerada != null -> 2
                    else -> 0
                },
                label = "AiStateTransition",
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) }
            ) { targetState ->
                when (targetState) {
                    1 -> {
                        val frasesCarga = listOf(
                            "Iniciando red neuronal...",
                            "Analizando tu perfil biométrico...",
                            "Cruzando datos con la base de ejercicios...",
                            "Calculando volumen y descansos óptimos...",
                            "Compilando la rutina perfecta..."
                        )
                        var fraseIndex by remember { mutableIntStateOf(0) }

                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(2500)
                                fraseIndex = (fraseIndex + 1) % frasesCarga.size
                            }
                        }

                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "iconPulse"
                        )

                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                                    .border(4.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.SmartToy, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.tertiary)
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                            AnimatedContent(targetState = frasesCarga[fraseIndex], label = "textoCarga") { frase ->
                                Text(
                                    text = frase,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(0.7f).height(8.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.tertiary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    2 -> {
                        val rutina = state.rutinaGenerada!!
                        Column(modifier = Modifier.fillMaxSize()) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiaryContainer).padding(16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Forjada por Inteligencia Artificial", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(rutina.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(rutina.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Plan de Entrenamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(rutina.ejercicios) { ej ->
                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(ej.nombre, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("${ej.grupoMuscular} • ${ej.series}x${ej.repeticiones} • Descanso: ${ej.descansoSegundos}s", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.SmartToy, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("¿Qué deseas lograr hoy?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Text("AtlasPath diseñará el plan perfecto.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = state.prompt,
                                onValueChange = { onEvent(AiCreatorEvent.OnPromptChange(it)) },
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                placeholder = { Text("Ej: Acondicionamiento físico para mejorar en boxeo, usando solo mancuernas, 45 minutos...") },
                                maxLines = 8,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            )

                            AnimatedVisibility(
                                visible = state.prompt.isBlank(),
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text("Sugerencias rápidas:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val sugerencias = listOf("En casa", "Solo mancuernas", "Hipertrofia", "Fuerza máxima", "30 minutos", "Cardio intenso")
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        sugerencias.forEach { sug ->
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    val nuevoPrompt = if (state.prompt.isBlank()) sug else "${state.prompt}, $sug"
                                                    onEvent(AiCreatorEvent.OnPromptChange(nuevoPrompt))
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surface,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Text(text = "+ $sug", modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }

                            state.error?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                ) {
                                    Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = { onEvent(AiCreatorEvent.GenerateRoutine) },
                                modifier = Modifier.fillMaxWidth().height(64.dp).padding(bottom = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                enabled = state.prompt.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (state.prompt.isNotBlank()) 6.dp else 0.dp)
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Generar Magia", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiCreatorInitialPreview() {
    MaterialTheme {
        Surface {
            AiCreatorBodyScreen(
                state = AiCreatorUiState(prompt = "Acondicionamiento físico..."),
                onEvent = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiCreatorLoadingPreview() {
    MaterialTheme {
        Surface {
            AiCreatorBodyScreen(
                state = AiCreatorUiState(isLoading = true),
                onEvent = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiCreatorSuccessPreview() {
    MaterialTheme {
        Surface {
            val rutinaMock = Rutina(
                titulo = "Fuerza Espartana",
                descripcion = "Rutina intensa para cuerpo completo generada por IA.",
                ejercicios = listOf(
                    Ejercicio("Sentadillas", 4, 12, 60, "Piernas"),
                    Ejercicio("Dominadas", 4, 10, 90, "Espalda")
                )
            )
            AiCreatorBodyScreen(
                state = AiCreatorUiState(rutinaGenerada = rutinaMock),
                onEvent = {},
                onBack = {}
            )
        }
    }
}