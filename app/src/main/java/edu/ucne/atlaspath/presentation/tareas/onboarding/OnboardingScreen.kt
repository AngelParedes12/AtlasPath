package edu.ucne.atlaspath.presentation.tareas.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.success) {
        if (state.success) {
            onFinish()
        }
    }

    OnboardingBodyScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingBodyScreen(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FitnessCenter, "Logo", modifier = Modifier.fillMaxSize(), tint = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("ATLASPATH", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, letterSpacing = 4.sp, color = MaterialTheme.colorScheme.primary)
        Text("Forja tu leyenda.\nTransforma tu cuerpo.", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = state.nombre,
            onValueChange = { onEvent(OnboardingEvent.OnNombreChange(it)) },
            label = { Text("¿Cuál es tu nombre, Atleta?") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Tu Nivel de Experiencia:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val niveles = listOf("Principiante", "Intermedio", "Avanzado")
            niveles.forEach { nivelStr ->
                FilterChip(
                    selected = state.nivel == nivelStr,
                    onClick = { onEvent(OnboardingEvent.OnNivelChange(nivelStr)) },
                    label = { Text(nivelStr) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Tu Objetivo Principal:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val objetivos = listOf("Hipertrofia", "Fuerza", "Resistencia", "Pérdida de Peso")
            objetivos.forEach { objStr ->
                FilterChip(
                    selected = state.objetivo == objStr,
                    onClick = { onEvent(OnboardingEvent.OnObjetivoChange(objStr)) },
                    label = { Text(objStr) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                    )
                )
            }
        }
        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onEvent(OnboardingEvent.FinalizarOnboarding) },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(24.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Comenzar mi Viaje", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    MaterialTheme {
        Surface {
            OnboardingBodyScreen(
                state = OnboardingUiState(
                    nombre = "",
                    nivel = "Principiante",
                    objetivo = "Hipertrofia"
                ),
                onEvent = {}
            )
        }
    }
}