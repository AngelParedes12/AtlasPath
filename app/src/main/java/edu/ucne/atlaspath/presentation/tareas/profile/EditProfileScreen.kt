@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
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
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost
import kotlinx.coroutines.launch

// Mock de Estado y Eventos para la UI (Reemplazar con tu ViewModel real si lo tienes)



@Composable
fun EditProfileScreen(
    // viewModel: EditProfileViewModel = hiltViewModel(), // Descomentar al conectar tu lógica
    onBack: () -> Unit
) {
    // val state by viewModel.state.collectAsStateWithLifecycle()
    var state by remember { mutableStateOf(EditProfileUiState()) } // Estado temporal para UI
    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHost.showSnackbar("✅ Perfil de Atleta actualizado")
            onBack()
        }
    }

    EditProfileBodyScreen(
        state = state,
        onEvent = { event ->
            // Simulador de ViewModel para que el Preview y la UI funcionen
            when (event) {
                is EditProfileEvent.OnNombreChange -> state = state.copy(nombre = event.nombre)
                is EditProfileEvent.OnPesoChange -> state = state.copy(pesoLbs = event.peso)
                is EditProfileEvent.OnAlturaChange -> state = state.copy(alturaCm = event.altura)
                is EditProfileEvent.OnNivelChange -> state = state.copy(nivel = event.nivel)
                is EditProfileEvent.OnObjetivoChange -> state = state.copy(objetivo = event.objetivo)
                is EditProfileEvent.SaveProfile -> {
                    scope.launch {
                        state = state.copy(isLoading = true)
                        kotlinx.coroutines.delay(800)
                        state = state.copy(isLoading = false, isSaved = true)
                    }
                }
            }
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileBodyScreen(
    state: EditProfileUiState,
    onEvent: (EditProfileEvent) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajustar Parámetros", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onEvent(EditProfileEvent.SaveProfile) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = state.nombre.isNotBlank() && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Identidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { onEvent(EditProfileEvent.OnNombreChange(it)) },
                placeholder = { Text("Nombre de Atleta") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            Text("Biometría Actual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Peso", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("${state.pesoLbs.toInt()} lbs", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = state.pesoLbs,
                        onValueChange = { onEvent(EditProfileEvent.OnPesoChange(it)) },
                        valueRange = 90f..350f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer), contentAlignment = Alignment.Center) {
                                Text("↕", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Altura", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("${state.alturaCm.toInt()} cm", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = state.alturaCm,
                        onValueChange = { onEvent(EditProfileEvent.OnAlturaChange(it)) },
                        valueRange = 140f..220f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.tertiary, activeTrackColor = MaterialTheme.colorScheme.tertiary)
                    )
                }
            }

            Text("Objetivo del Santuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val objetivos = listOf("Hipertrofia", "Fuerza", "Resistencia", "Pérdida de Peso")
                objetivos.forEach { objStr ->
                    val isSelected = state.objetivo == objStr
                    val containerColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, label = "color")
                    val contentColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, label = "contentColor")
                    val elevation by animateDpAsState(if (isSelected) 4.dp else 0.dp, label = "elevation")

                    Surface(
                        modifier = Modifier.clickable { onEvent(EditProfileEvent.OnObjetivoChange(objStr)) },
                        shape = RoundedCornerShape(12.dp),
                        color = containerColor,
                        shadowElevation = elevation,
                        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(text = objStr, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                }
            }

            Text("Rango Actual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val niveles = listOf("Principiante", "Intermedio", "Avanzado")
                niveles.forEach { nivelStr ->
                    val isSelected = state.nivel == nivelStr
                    val containerColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant, label = "color")
                    val contentColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant, label = "contentColor")
                    val elevation by animateDpAsState(if (isSelected) 4.dp else 0.dp, label = "elevation")

                    Surface(
                        modifier = Modifier.clickable { onEvent(EditProfileEvent.OnNivelChange(nivelStr)) },
                        shape = RoundedCornerShape(12.dp),
                        color = containerColor,
                        shadowElevation = elevation,
                        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(text = nivelStr, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                EditProfileScreen(onBack = {})
            }
        }
    }
}