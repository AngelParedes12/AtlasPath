package edu.ucne.atlaspath.presentation.tareas.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.RegistroEjercicio
import edu.ucne.atlaspath.domain.model.Sesion
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HistoryBodyScreen(
        state = state,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBodyScreen(
    state: HistoryUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de Batalla", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistoryStatCard(
                        label = "XP Total",
                        value = "${state.xpAcumulada}",
                        icon = Icons.Default.EmojiEvents,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    HistoryStatCard(
                        label = "Tonelaje Total",
                        value = "${(state.volumenTotalHistorico / 1000).toInt()}k lbs",
                        icon = Icons.Default.BarChart,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Text("Entrenamientos Pasados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (state.sesiones.isEmpty() && !state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Aún no has librado ninguna batalla.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            items(state.sesiones) { sesion ->
                SessionHistoryCard(sesion)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun SessionHistoryCard(sesion: Sesion) {
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale("es", "ES"))
    val fecha = sdf.format(Date(sesion.fechaInicio)).replaceFirstChar { it.uppercase() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(fecha, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text("Sesión de Entrenamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+${sesion.xpGanada} XP",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SmallStat(label = "Volumen", value = "${sesion.volumenTotalLbs.toInt()} lbs")
                SmallStat(label = "Ejercicios", value = "${sesion.registros.distinctBy { it.ejercicioNombre }.size}")

                val duracionMinutos = if (sesion.fechaFin > sesion.fechaInicio) {
                    (sesion.fechaFin - sesion.fechaInicio) / 60000
                } else 0
                SmallStat(label = "Tiempo", value = "${duracionMinutos} min")
            }
        }
    }
}

@Composable
fun SmallStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HistoryStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryEmptyPreview() {
    MaterialTheme {
        Surface {
            HistoryBodyScreen(
                state = HistoryUiState(
                    xpAcumulada = 0,
                    volumenTotalHistorico = 0.0,
                    sesiones = emptyList(),
                    isLoading = false
                ),
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPopulatedPreview() {
    MaterialTheme {
        Surface {
            val now = Calendar.getInstance().timeInMillis
            val mockSesion1 = Sesion(
                sesionId = 1,
                rutinaId = 1,
                fechaInicio = now - 3600000, // Hace 1 hora
                fechaFin = now,
                volumenTotalLbs = 12500.0,
                xpGanada = 850,
                registros = listOf(
                    RegistroEjercicio("Press de Banca", "Pecho", 135.0, 10),
                    RegistroEjercicio("Aperturas", "Pecho", 40.0, 12)
                )
            )

            HistoryBodyScreen(
                state = HistoryUiState(
                    xpAcumulada = 850,
                    volumenTotalHistorico = 12500.0,
                    sesiones = listOf(mockSesion1),
                    isLoading = false
                ),
                onBack = {}
            )
        }
    }
}