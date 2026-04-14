@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                Spacer(modifier = Modifier.height(8.dp))
                Text("Entrenamientos Pasados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }

            if (state.sesiones.isEmpty() && !state.isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aún no has librado ninguna batalla.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Tus entrenamientos aparecerán aquí.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(fecha, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sesión de Entrenamiento", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                }
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "+${sesion.xpGanada} XP",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                val duracionMinutos = if (sesion.fechaFin > sesion.fechaInicio) {
                    (sesion.fechaFin - sesion.fechaInicio) / 60000
                } else 0

                SmallStat(icon = Icons.Default.BarChart, label = "Volumen", value = "${sesion.volumenTotalLbs.toInt()} lbs")
                SmallStat(icon = Icons.Default.FitnessCenter, label = "Ejercicios", value = "${sesion.registros.distinctBy { it.ejercicioNombre }.size}")
                SmallStat(icon = Icons.Default.Timer, label = "Tiempo", value = "${duracionMinutos} min")
            }
        }
    }
}

@Composable
fun SmallStat(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun HistoryStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.8f))
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
                fechaInicio = now - 3600000,
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