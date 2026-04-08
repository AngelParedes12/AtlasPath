package edu.ucne.atlaspath.presentation.tareas.aicreator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.delay

@Composable
fun AiCreatorScreen(
    viewModel: AiCreatorViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToLiveWorkout: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("IA Creador", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
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
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
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
                                    .size(100.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.SmartToy, contentDescription = null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.tertiary)
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            AnimatedContent(targetState = frasesCarga[fraseIndex], label = "textoCarga") { frase ->
                                Text(
                                    text = frase,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(0.6f).height(6.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    2 -> {
                        val rutina = state.rutinaGenerada!!
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.SmartToy, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Rutina Generada Exitosamente", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(rutina.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(rutina.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Plan de Entrenamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(rutina.ejercicios) { ej ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.tertiaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.FitnessCenter, null, tint = MaterialTheme.colorScheme.tertiary)
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(ej.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                Text("${ej.grupoMuscular} | ${ej.series} Series x ${ej.repeticiones} Reps", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { onEvent(AiCreatorEvent.DiscardRoutine) },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Descartar", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { onEvent(AiCreatorEvent.SaveGeneratedRoutine) },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Filled.SmartToy, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("¿Qué deseas lograr hoy?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                            Text("Sé lo más específico posible. AtlasPath diseñará el plan perfecto para tu objetivo.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = state.prompt,
                                onValueChange = { onEvent(AiCreatorEvent.OnPromptChange(it)) },
                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                placeholder = { Text("Ej: Acondicionamiento físico general para mejorar en boxeo, usando solo mancuernas, 45 minutos...") },
                                maxLines = 6,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Sugerencias rápidas:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

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
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    ) {
                                        Text(text = "+ $sug", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }

                            state.error?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                                    Text(it, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = { onEvent(AiCreatorEvent.GenerateRoutine) },
                                modifier = Modifier.fillMaxWidth().height(64.dp),
                                shape = RoundedCornerShape(24.dp),
                                enabled = state.prompt.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                )
                            ) {
                                Icon(Icons.Filled.SmartToy, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generar Magia", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
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

@Preview(showBackground = true)
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