@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.calendario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.presentation.tareas.history.HistoryUiState
import edu.ucne.atlaspath.presentation.tareas.history.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FullCalendarScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                title = { Text("Calendario", fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val prev = currentMonth.clone() as Calendar
                                prev.add(Calendar.MONTH, -1)
                                currentMonth = prev
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) { Icon(Icons.Default.ChevronLeft, "Mes Anterior") }

                        Text(
                            text = SimpleDateFormat("MMMM yyyy", Locale("es", "ES")).format(currentMonth.time).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(
                            onClick = {
                                val next = currentMonth.clone() as Calendar
                                next.add(Calendar.MONTH, 1)
                                currentMonth = next
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) { Icon(Icons.Default.ChevronRight, "Mes Siguiente") }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEach {
                            Text(it, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    val daysInMonth = getDaysForMonth(currentMonth)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(daysInMonth) { dayInfo ->
                            if (dayInfo.dayNumber == 0) {
                                Spacer(modifier = Modifier.aspectRatio(1f))
                            } else {
                                val entrenoEsteDia = state.sesiones.any { sesion ->
                                    val sCal = Calendar.getInstance().apply { timeInMillis = sesion.fechaInicio }
                                    sCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                            sCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                            sCal.get(Calendar.DAY_OF_MONTH) == dayInfo.dayNumber
                                }

                                val isToday = Calendar.getInstance().let {
                                    it.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                            it.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                            it.get(Calendar.DAY_OF_MONTH) == dayInfo.dayNumber
                                }

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                entrenoEsteDia -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.surfaceVariant
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = if (isToday && !entrenoEsteDia) 2.dp else 0.dp,
                                            color = if (isToday && !entrenoEsteDia) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayInfo.dayNumber.toString(),
                                        fontWeight = if (entrenoEsteDia || isToday) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 16.sp,
                                        color = when {
                                            entrenoEsteDia -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.EventNote, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Consistencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Los días marcados reflejan tu disciplina.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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