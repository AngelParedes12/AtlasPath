package edu.ucne.atlaspath.domain.model

data class Rutina(
    val rutinaId: Int = 0,
    val titulo: String,
    val descripcion: String,
    val ejercicios: List<Ejercicio>
)

data class Ejercicio(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descansoSegundos: Int,
    val grupoMuscular: String,
    val gifUrl: String? = null,
    val instrucciones: List<String>? = null
)

data class EjercicioCatalog(
    val id: String,
    val nombre: String,
    val grupoMuscular: String,
    val imageUrl: String
)

object CatalogoEjercicios {
    val lista = listOf(
        EjercicioCatalog("1", "Press de Banca", "Pecho", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&q=80"),
        EjercicioCatalog("2", "Sentadilla con Barra", "Piernas", "https://images.unsplash.com/photo-1566241440091-ec10de8db2e1?w=400&q=80"),
        EjercicioCatalog("3", "Peso Muerto", "Piernas", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80"),
        EjercicioCatalog("4", "Curl de Bíceps", "Brazos", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=400&q=80"),
        EjercicioCatalog("5", "Dominadas", "Espalda", "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=400&q=80"),
        EjercicioCatalog("6", "Press Militar", "Hombros", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80"), // Placeholder
        EjercicioCatalog("7", "Plancha (Plank)", "Core", "https://images.unsplash.com/photo-1566241477600-ac0240434106?w=400&q=80")
    )
}