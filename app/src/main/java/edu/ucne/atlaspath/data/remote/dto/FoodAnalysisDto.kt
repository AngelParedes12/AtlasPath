package edu.ucne.atlaspath.data.remote.dto

import com.squareup.moshi.JsonClass
import edu.ucne.atlaspath.domain.model.RegistroNutricional
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@JsonClass(generateAdapter = true)
data class FoodAnalysisDto(
    val comidaTexto: String,
    val calorias: Int,
    val proteina: Float,
    val carbohidratos: Float,
    val grasa: Float
) {
    fun toDomain(): RegistroNutricional {
        val timestampActual = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaFormateada = sdf.format(Date(timestampActual))

        return RegistroNutricional(
            comidaTexto = comidaTexto,
            calorias = calorias,
            proteina = proteina,
            carbohidratos = carbohidratos,
            grasa = grasa,
            fechaFormateada = fechaFormateada,
            timestamp = timestampActual
        )
    }
}