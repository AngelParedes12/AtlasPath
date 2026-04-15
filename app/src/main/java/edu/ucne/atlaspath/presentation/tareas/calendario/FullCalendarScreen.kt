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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.presentation.tareas.history.HistoryUiState
import edu.ucne.atlaspath.presentation.tareas.history.HistoryViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendario", fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    CalendarHeader(
                        currentMonth = currentMonth,
                        onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
                        onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    DaysOfWeekRow()
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    CalendarGrid(
                        currentMonth = currentMonth,
                        sesiones = state.sesiones
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            ConsistencyLegendCard()
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
    val monthName = currentMonth.format(formatter).replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevMonth,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }

        Text(
            text = monthName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
    }
}

@Composable
fun DaysOfWeekRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEach {
            Text(it, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    sesiones: List<Sesion>
) {
    val daysInMonth = getDaysForMonth(currentMonth)

    val trainingDates = remember(sesiones) {
        sesiones.map {
            Instant.ofEpochMilli(it.fechaInicio).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysInMonth) { dayInfo ->
            if (dayInfo.dayNumber == 0) {
                Spacer(modifier = Modifier.aspectRatio(1f))
            } else {
                val currentDate = currentMonth.atDay(dayInfo.dayNumber)
                CalendarDayCell(
                    date = currentDate,
                    isTrained = trainingDates.contains(currentDate)
                )
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    isTrained: Boolean
) {
    val isToday = date == LocalDate.now()

    val bgColor = when {
        isTrained -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val borderColor = if (isToday && !isTrained) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderWidth = if (isToday && !isTrained) 2.dp else 0.dp

    val textColor = when {
        isTrained -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val textWeight = if (isTrained || isToday) FontWeight.Black else FontWeight.Medium

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = textWeight,
            fontSize = 16.sp,
            color = textColor
        )
    }
}

@Composable
fun ConsistencyLegendCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
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

data class DayInfo(val dayNumber: Int)

fun getDaysForMonth(yearMonth: YearMonth): List<DayInfo> {
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // 1=Mon, 7=Sun
    val emptyDaysCount = firstDayOfWeek - 1
    val maxDays = yearMonth.lengthOfMonth()

    val daysList = mutableListOf<DayInfo>()
    for (i in 0 until emptyDaysCount) daysList.add(DayInfo(0))
    for (i in 1..maxDays) daysList.add(DayInfo(i))

    return daysList
}

@Preview(showBackground = true)
@Composable
fun FullCalendarPreview() {
    MaterialTheme {
        Surface {
            val today = Instant.now().toEpochMilli()
            val yesterday = Instant.now().minusSeconds(86400).toEpochMilli()

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