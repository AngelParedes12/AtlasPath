```markdown
# 🏋️‍♂️ AtlasPath - Forja Tu Leyenda

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4.svg?style=flat&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![Dagger Hilt](https://img.shields.io/badge/Dagger_Hilt-DI-34A853.svg?style=flat)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Room-Database-F4B400.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-Clean_%2B_MVI-ea4335.svg?style=flat)]()

**AtlasPath** es una aplicación de fitness gamificada construida con los más altos estándares de desarrollo en Android. Diseñada para transformar el entrenamiento diario en una experiencia RPG, permite a los usuarios subir de nivel, ganar experiencia (XP) y obtener rangos musculares basándose en el volumen total levantado.

Esta aplicación fue desarrollada siguiendo estrictamente la **Clean Architecture**, con un flujo de datos unidireccional (**UDF / MVI**) y priorizando el funcionamiento sin conexión (**Offline First**) mediante una base de datos local como única fuente de verdad (Single Source of Truth).

---

## ✨ Características Principales

* **🏆 Gamificación del Entrenamiento:** Sistema de niveles, cálculo de experiencia (XP) y rangos por grupo muscular (Hierro, Bronce, Plata, Oro, Platino) basados en el volumen de peso levantado.
* **🤖 Creador de Rutinas con IA:** Integración con la API de **Google Gemini** para generar rutinas de entrenamiento personalizadas basadas en lenguaje natural ("Quiero mejorar mi fuerza en piernas en 45 minutos").
* **📚 Biblioteca Global de Ejercicios:** Explorador de ejercicios conectado a la API de **ExerciseDB** (RapidAPI) con traductor automático español-inglés, permitiendo buscar y añadir ejercicios con animaciones (GIFs) e instrucciones.
* **🏋️‍♂️ Seguimiento en Tiempo Real (Live Workout):** Cronómetro integrado, registro interactivo de series (Sets), repeticiones y peso (LBs), con cálculo automático del volumen total de la sesión.
* **📊 Historial y Dashboard:** Gráficos de progreso, calendario visual de los días entrenados y resumen de las sesiones pasadas.
* **📱 Diseño Moderno (Material Design 3):** Interfaz fluida construida 100% con **Jetpack Compose**.
* **🔗 Compartir Logros:** Capacidad para generar una "Tarjeta de Atleta" (Bitmap) y compartir rutinas como texto nativo del sistema.

---

## 🏗️ Arquitectura y Tecnologías

El proyecto está dividido en tres capas principales (Clean Architecture):

1.  **Capa de Presentación (Presentation):** * **UI:** Jetpack Compose (M3) puro, sin lógica de negocio.
    * **Gestión de Estado:** Arquitectura MVI (Model-View-Intent) utilizando `ViewModel`, `UiState` y eventos sellados (Sealed Interfaces) mediante `StateFlow`.
    * **Navegación:** Type-Safe Navigation (Jetpack Navigation Component v2.8+ con Kotlin Serialization).
2.  **Capa de Dominio (Domain):**
    * Modelos de datos puros de Kotlin (`Rutina`, `Sesion`, `Ejercicio`).
    * Casos de Uso (Use Cases) enfocados en una única responsabilidad (Ej. `GetDashboardStatsUseCase`, `SaveSesionUseCase`).
    * Interfaces de Repositorios (Inversión de Dependencias).
3.  **Capa de Datos (Data):**
    * **Local (SSOT):** Base de datos Room para persistencia offline y DataStore (Preferences) para el perfil del usuario.
    * **Remoto:** Consumo de APIs REST (Google Gemini y ExerciseDB) utilizando Retrofit, Moshi para el parseo JSON y un patrón `Resource` (Success/Error/Loading) para manejar estados de red.
    * Implementación de Repositorios que combinan datos locales y remotos.

### 🧰 Librerías Clave
* **Inyección de Dependencias:** Dagger Hilt
* **Concurrencia:** Kotlin Coroutines & Flow
* **Red:** Retrofit2 + OkHttp + Moshi
* **Imágenes:** Coil (soporte para GIFs)
* **Captura de UI:** Capturable (para exportar tarjetas de perfil)

---

## 🚀 Guía de Instalación y Configuración

Para garantizar la seguridad de las claves de API y evitar filtraciones en el control de versiones, el proyecto utiliza el archivo `local.properties` inyectado mediante `BuildConfig`.

### 1. Clonar el repositorio
```bash
git clone [https://github.com/TU_USUARIO/AtlasPath.git](https://github.com/TU_USUARIO/AtlasPath.git)
cd AtlasPath
```

### 2. Configurar las API Keys
Este proyecto requiere dos claves API gratuitas. 
1.  **Google Gemini API:** Obtenla en [Google AI Studio](https://aistudio.google.com/app/apikey).
2.  **ExerciseDB API:** Suscríbete al plan gratuito (Basic) en [RapidAPI](https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb).

Una vez que tengas tus claves, abre o crea el archivo **`local.properties`** en el directorio raíz del proyecto (al mismo nivel que `app/` y `build.gradle.kts`) y añade las siguientes líneas sin comillas:

```properties
# Mis llaves secretas (No se suben a GitHub)
EXERCISE_DB_KEY=tu_llave_de_rapid_api_aqui
GEMINI_API_KEY=tu_llave_de_google_gemini_aqui
```

*(Nota: Este archivo está ignorado por defecto en el `.gitignore`, por lo que tus claves estarán seguras localmente).*

### 3. Compilar el Proyecto
Abre el proyecto en **Android Studio (Iguana o superior recomendado)**.
1.  Espera a que Gradle sincronice las dependencias.
2.  Ve a `Build` -> `Clean Project`.
3.  Ve a `Build` -> `Rebuild Project` (esto generará el archivo `BuildConfig` con tus claves).
4.  Ejecuta la aplicación (Shift + F10) en un emulador o dispositivo físico.

---

## 👨‍💻 Autor

**Angel Paredes Almonte** *Estudiante de Ingeniería en la Universidad Católica Nordestana (UCNE).*

Este proyecto fue desarrollado como parte de la asignatura de **Aplicada II**, demostrando el dominio de patrones de diseño avanzados, desarrollo de UI declarativa y consumo de APIs en el ecosistema Android moderno.

---

> *"Levanta pesado, codifica limpio."* 🛡️
```
