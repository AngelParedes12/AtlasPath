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

@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {
    var state by remember { mutableStateOf(EditProfileUiState()) }
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

@Composable
fun EditProfileBodyScreen(
    state: EditProfileUiState,
    onEvent: (EditProfileEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { EditProfileTopBar(onBack = onBack) },
        bottomBar = {
            EditProfileBottomBar(
                isLoading = state.isLoading,
                isNameValid = state.nombre.isNotBlank(),
                onSave = { onEvent(EditProfileEvent.SaveProfile) }
            )
        }
    ) { padding ->
        EditProfileFormContent(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun EditProfileFormContent(
    state: EditProfileUiState,
    onEvent: (EditProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        IdentitySection(
            nombre = state.nombre,
            onNombreChange = { onEvent(EditProfileEvent.OnNombreChange(it)) }
        )

        BiometrySection(
            pesoLbs = state.pesoLbs,
            alturaCm = state.alturaCm,
            onPesoChange = { onEvent(EditProfileEvent.OnPesoChange(it)) },
            onAlturaChange = { onEvent(EditProfileEvent.OnAlturaChange(it)) }
        )

        SelectionFlowRow(
            title = "Objetivo del Santuario",
            options = listOf("Hipertrofia", "Fuerza", "Resistencia", "Pérdida de Peso"),
            selectedOption = state.objetivo,
            activeColor = MaterialTheme.colorScheme.primary,
            onActiveColor = MaterialTheme.colorScheme.onPrimary,
            onSelect = { onEvent(EditProfileEvent.OnObjetivoChange(it)) }
        )

        SelectionFlowRow(
            title = "Rango Actual",
            options = listOf("Principiante", "Intermedio", "Avanzado"),
            selectedOption = state.nivel,
            activeColor = MaterialTheme.colorScheme.tertiary,
            onActiveColor = MaterialTheme.colorScheme.onTertiary,
            onSelect = { onEvent(EditProfileEvent.OnNivelChange(it)) }
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Ajustar Parámetros", fontWeight = FontWeight.Black) },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
        }
    )
}

@Composable
fun EditProfileBottomBar(isLoading: Boolean, isNameValid: Boolean, onSave: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isNameValid && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
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

@Composable
fun IdentitySection(nombre: String, onNombreChange: (String) -> Unit) {
    Column {
        Text("Identidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
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
    }
}

// --- CLASE DE CONFIGURACIÓN PARA REDUCIR PARÁMETROS ---
data class SliderConfig(
    val unit: String,
    val iconBgColor: Color,
    val sliderColor: Color,
    val iconContent: @Composable () -> Unit
)

@Composable
fun BiometrySection(
    pesoLbs: Float,
    alturaCm: Float,
    onPesoChange: (Float) -> Unit,
    onAlturaChange: (Float) -> Unit
) {
    Column {
        Text("Biometría Actual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                val pesoConfig = SliderConfig(
                    unit = "lbs",
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                    sliderColor = MaterialTheme.colorScheme.primary,
                    iconContent = { Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp)) }
                )

                BiometrySliderRow(
                    label = "Peso",
                    value = pesoLbs,
                    range = 90f..350f,
                    config = pesoConfig,
                    onValueChange = onPesoChange
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))

                val alturaConfig = SliderConfig(
                    unit = "cm",
                    iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                    sliderColor = MaterialTheme.colorScheme.tertiary,
                    iconContent = { Text("↕", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                )

                BiometrySliderRow(
                    label = "Altura",
                    value = alturaCm,
                    range = 140f..220f,
                    config = alturaConfig,
                    onValueChange = onAlturaChange
                )
            }
        }
    }
}

@Composable
fun BiometrySliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    config: SliderConfig,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(config.iconBgColor), contentAlignment = Alignment.Center) {
                    config.iconContent()
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("${value.toInt()} ${config.unit}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = config.sliderColor)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(thumbColor = config.sliderColor, activeTrackColor = config.sliderColor)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectionFlowRow(
    title: String,
    options: List<String>,
    selectedOption: String,
    activeColor: Color,
    onActiveColor: Color,
    onSelect: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { optStr ->
                val isSelected = selectedOption == optStr
                SelectionOptionChip(
                    text = optStr,
                    isSelected = isSelected,
                    activeColor = activeColor,
                    onActiveColor = onActiveColor,
                    onClick = { onSelect(optStr) }
                )
            }
        }
    }
}

@Composable
private fun SelectionOptionChip(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    onActiveColor: Color,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant, label = "color")
    val contentColor by animateColorAsState(if (isSelected) onActiveColor else MaterialTheme.colorScheme.onSurfaceVariant, label = "contentColor")
    val elevation by animateDpAsState(if (isSelected) 4.dp else 0.dp, label = "elevation")

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        shadowElevation = elevation,
        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
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