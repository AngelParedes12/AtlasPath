package edu.ucne.atlaspath.presentation.tareas.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.presentation.tareas.history.HistoryUiState // Asegúrate de que el path sea correcto
import edu.ucne.atlaspath.presentation.tareas.history.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FullCalendarScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Llamamos a la pantalla Stateless (UDF puro)
    FullCalendarBodyScreen(state = state, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCalendarBodyScreen(
    state: HistoryUiState,
    onBack: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendario de Batallas", fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val prev = currentMonth.clone() as Calendar
                    prev.add(Calendar.MONTH, -1)
                    currentMonth = prev
                }) { Icon(Icons.Default.ChevronLeft, "Mes Anterior") }

                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale("es", "ES")).format(currentMonth.time).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = {
                    val next = currentMonth.clone() as Calendar
                    next.add(Calendar.MONTH, 1)
                    currentMonth = next
                }) { Icon(Icons.Default.ChevronRight, "Mes Siguiente") }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach {
                    Text(it, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val daysInMonth = getDaysForMonth(currentMonth)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysInMonth) { dayInfo ->
                    if (dayInfo.dayNumber == 0) {
                        Spacer(modifier = Modifier.size(40.dp))
                    } else {
                        val entrenoEsteDia = state.sesiones.any { sesion ->
                            val sCal = Calendar.getInstance().apply { timeInMillis = sesion.fechaInicio }
                            sCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                    sCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                    sCal.get(Calendar.DAY_OF_MONTH) == dayInfo.dayNumber
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    if (entrenoEsteDia) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayInfo.dayNumber.toString(),
                                fontWeight = FontWeight.Bold,
                                color = if (entrenoEsteDia) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DayInfo(val dayNumber: Int)
fun getDaysForMonth(calendar: Calendar): List<DayInfo> {
    val tempCal = calendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)

    var firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 2
    if (firstDayOfWeek < 0) firstDayOfWeek = 6

    val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val daysList = mutableListOf<DayInfo>()
    for (i in 0 until firstDayOfWeek) daysList.add(DayInfo(0))
    for (i in 1..maxDays) daysList.add(DayInfo(i))

    return daysList
}

@Preview(showBackground = true)
@Composable
fun FullCalendarPreview() {
    MaterialTheme {
        Surface {
            val today = Calendar.getInstance().timeInMillis
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.timeInMillis

            val mockSesiones = listOf(
                Sesion(fechaInicio = today, rutinaId = 1, fechaFin = today + 3600),
                Sesion(fechaInicio = yesterday, rutinaId = 2, fechaFin = yesterday + 3600)
            )

            FullCalendarBodyScreen(
                state = HistoryUiState(sesiones = mockSesiones),
                onBack = {}
            )
        }
    }
}