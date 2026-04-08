package edu.ucne.atlaspath.presentation.onboarding

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onFinish: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundBrush).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FitnessCenter, "Logo", modifier = Modifier.fillMaxSize(), tint = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("ATLASPATH", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, letterSpacing = 4.sp, color = MaterialTheme.colorScheme.primary)
        Text("Forja tu leyenda.\nTransforma tu cuerpo.", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
                if (it.isNotBlank()) isError = false
            },
            label = { Text("¿Cuál es tu nombre, Atleta?") },
            isError = isError,
            supportingText = {
                if (isError) Text("El nombre es necesario para tu perfil de atleta")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (nameInput.isNotBlank()) onFinish(nameInput) else isError = true
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Comenzar mi Viaje", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}