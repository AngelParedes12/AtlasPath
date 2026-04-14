package edu.ucne.atlaspath.data.local.mapper

import edu.ucne.atlaspath.data.local.entity.FoodEntity
import edu.ucne.atlaspath.domain.model.RegistroNutricional

fun FoodEntity.toDomain(): RegistroNutricional {
    return RegistroNutricional(
        id = id,
        comidaTexto = foodText,
        calorias = calories,
        proteina = protein,
        carbohidratos = carbs,
        grasa = fat,
        fechaFormateada = dateString,
        timestamp = timestamp
    )
}

fun RegistroNutricional.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        foodText = comidaTexto,
        calories = calorias,
        protein = proteina,
        carbs = carbohidratos,
        fat = grasa,
        dateString = fechaFormateada,
        timestamp = timestamp
    )
}