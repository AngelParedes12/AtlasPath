@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.atlaspath.presentation.tareas.nutrition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import edu.ucne.atlaspath.data.remote.dto.RecipeDto
import edu.ucne.atlaspath.domain.model.RegistroNutricional
import edu.ucne.atlaspath.presentation.tareas.navigation.LocalSnackbarHost
import kotlinx.coroutines.launch

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NutritionBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
fun NutritionBodyScreen(
    state: NutritionUiState,
    onEvent: (NutritionEvent) -> Unit,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Registro Diario", "Chef IA")

    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            NutritionTopBar(
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                onTabSelected = { selectedTabIndex = it },
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (selectedTabIndex == 0) {
                item {
                    MacrosDashboard(
                        calories = state.totalCalories,
                        protein = state.totalProtein,
                        carbs = state.totalCarbs,
                        fat = state.totalFat
                    )
                }

                if (state.dailyCalorieGoal > 0 && state.totalCalories >= state.dailyCalorieGoal) {
                    item {
                        CalorieWarningCard(
                            caloriasActuales = state.totalCalories,
                            meta = state.dailyCalorieGoal
                        )
                    }
                }

                item {
                    FoodInputSection(state = state, onEvent = onEvent)
                }

                item {
                    DailyRecordsHeader()
                }

                if (state.dailyRecords.isEmpty() && !state.isLoading) {
                    item {
                        EmptyDailyRecordsCard()
                    }
                } else {
                    items(state.dailyRecords) { record ->
                        FoodRecordItem(
                            record = record,
                            onDelete = {
                                onEvent(NutritionEvent.DeleteRecord(record.id))
                                scope.launch { snackbarHost.showSnackbar("🗑️ Registro de comida eliminado") }
                            }
                        )
                    }
                }
            } else {
                item {
                    RecipeInputSection(state = state, onEvent = onEvent)
                }

                if (state.generatedRecipe == null && !state.isGeneratingRecipe) {
                    item {
                        EmptyRecipeCard()
                    }
                } else if (state.generatedRecipe != null) {
                    item {
                        RecipeCard(recipe = state.generatedRecipe)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun NutritionTopBar(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Mi Nutrición", fontWeight = FontWeight.Black) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FoodInputSection(
    state: NutritionUiState,
    onEvent: (NutritionEvent) -> Unit
) {
    Column {
        Text(
            "¿Qué comiste hoy?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.foodInputText,
            onValueChange = { onEvent(NutritionEvent.OnFoodInputChanged(it)) },
            placeholder = { Text("Ej: 3 huevos, 2 tostadas integrales...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            maxLines = 3,
            leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onEvent(NutritionEvent.AnalyzeAndSaveFood) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state.foodInputText.isNotBlank() && !state.isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Analizando con IA...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calcular y Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        AnimatedVisibility(visible = state.error != null) {
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RecipeInputSection(
    state: NutritionUiState,
    onEvent: (NutritionEvent) -> Unit
) {
    Column {
        Text(
            "Crea una receta con lo que tienes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.recipeInputText,
            onValueChange = { onEvent(NutritionEvent.OnRecipeInputChanged(it)) },
            placeholder = { Text("Ej: Tengo pollo, arroz y brócoli...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            maxLines = 3,
            leadingIcon = { Icon(Icons.Default.LocalDining, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onEvent(NutritionEvent.GenerateRecipe) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state.recipeInputText.isNotBlank() && !state.isGeneratingRecipe,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (state.isGeneratingRecipe) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onTertiary, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cocinando receta...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generar Receta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DailyRecordsHeader() {
    Column {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Text(
            "Registro de Hoy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyDailyRecordsCard() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tu plato está vacío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Registra tu primera comida del día para empezar a sumar macros a tu objetivo.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EmptyRecipeCard() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LocalDining, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Esperando ingredientes...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dile al Chef IA qué tienes en tu cocina y te armará una receta alta en proteínas paso a paso.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CalorieWarningCard(caloriasActuales: Int, meta: Int) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alerta",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "¡Límite Calórico Alcanzado!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    "Has consumido $caloriasActuales kcal (Meta: $meta). Te sugerimos priorizar hidratación y proteínas ligeras por el resto del día.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeDto) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(20.dp)
            ) {
                Column {
                    Text(recipe.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(recipe.tiempoPreparacion, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("${recipe.caloriasEstimadas} kcal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("${recipe.proteinaEstimada}g Prot", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text("Ingredientes", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                recipe.ingredientes.forEach { ingrediente ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(ingrediente, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Instrucciones", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                recipe.instrucciones.forEachIndexed { index, paso ->
                    Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
                        Text("${index + 1}.", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(24.dp))
                        Text(paso, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun MacrosDashboard(calories: Int, protein: Float, carbs: Float, fat: Float) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TOTAL CONSUMIDO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Text("$calories kcal", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroCircle("Proteína", "${protein.toInt()}g", Color(0xFFE57373))
                MacroCircle("Carbos", "${carbs.toInt()}g", Color(0xFF64B5F6))
                MacroCircle("Grasa", "${fat.toInt()}g", Color(0xFFFFD54F))
            }
        }
    }
}

@Composable
fun MacroCircle(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(2.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(amount, fontWeight = FontWeight.ExtraBold, color = color, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun FoodRecordItem(record: RegistroNutricional, onDelete: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.comidaTexto, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${record.calorias} kcal • P: ${record.proteina}g • C: ${record.carbohidratos}g • G: ${record.grasa}g",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionScreenPreview() {
    MaterialTheme {
        Surface {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
                NutritionBodyScreen(
                    state = NutritionUiState(
                        totalCalories = 2600,
                        totalProtein = 120f,
                        totalCarbs = 200f,
                        totalFat = 80f,
                        dailyRecords = listOf(
                            RegistroNutricional(1, "Desayuno: Huevos y Avena", 450, 25f, 40f, 15f, "2026-04-14", 0L),
                            RegistroNutricional(2, "Almuerzo: Mega Hamburguesa", 2150, 95f, 160f, 65f, "2026-04-14", 0L)
                        )
                    ),
                    onEvent = {},
                    onBack = {}
                )
            }
        }
    }
}