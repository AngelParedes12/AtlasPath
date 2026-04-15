# 🏋️ AtlasPath – Forja tu leyenda, repetición a repetición

[](https://kotlinlang.org)
[](https://developer.android.com/jetpack/compose)
[](https://www.google.com/search?q=)
[](https://dagger.dev/hilt/)
[](https://opensource.org/licenses/MIT)

**¿Cansado de apps fitness que parecen hojas de cálculo aburridas?** Bienvenido a **AtlasPath**, donde cada serie cuenta una historia y cada PR (Personal Record) te acerca a convertirte en el **Titán del Gimnasio**.

Transforma tu entrenamiento en una **aventura RPG**:

  - Sube de nivel conforme levantas más peso.
  - Desbloquea **rangos musculares** (desde *Hierro Novato* hasta *Platino Legendario*).
  - Deja que una **IA (Gemini)** sea tu *Sensei* personal y te diseñe rutinas mientras tú te concentras en levantar hierro.

Desarrollada con la disciplina de un monje Shaolin del código (**Clean Architecture** y **MVI**), esta app funciona **incluso en el búnker más profundo sin señal** (Offline First). 📡❌

-----

## 🎮 El Juego (Características Principales)

### 🏆 **Sistema de Gamificación (El Grind)**

Tu esfuerzo tiene recompensa. No medimos calorías, medimos **GLORIA** (y volumen en LBs).

  - **XP por Levantamiento:** Cada serie suma experiencia a tus grupos musculares.
  - **Rangos de Poder:** ¿Eres *Hierro* en Pecho pero *Oro* en Pierna? AtlasPath te lo muestra sin piedad.
  - **Barra de Progreso Visual:** Porque ver cómo se llena la barra morada es más satisfactorio que el tercer café de la mañana.

### 🤖 **Creador de Rutinas con IA (Gemini Sensei)**

*"Quiero un entrenamiento de 45 minutos para espalda que me deje temblando como un flan"*.

  - Escribe lo que quieras en lenguaje natural.
  - **Google Gemini** te devuelve una rutina personalizada lista para ejecutar.
  - Adiós a los blocs de notas arrugados en el bolsillo.

### 🍎 **Coach Nutricional Inteligente (Chef IA)**

  - **Dashboard de Macros:** Control total de tus calorías, proteínas, carbohidratos y grasas con un diseño visual impecable.
  - **Registro Natural:** Dile a la IA *"Comí 3 huevos y 2 tostadas"* y ella calculará los macros exactos por ti.
  - **Generador de Recetas:** ¿No sabes qué cocinar? Dile a la IA qué ingredientes tienes en la nevera y forjará una receta alta en proteínas al instante.

### 📚 **Enciclopedia de Movimientos (ExerciseDB)**

  - **+1300 Ejercicios** con GIFs animados (para que no te lesiones haciendo el avestruz).
  - Buscador inteligente con **traducción automática** Español-Inglés (RapidAPI).
  - ¿No sabes qué es un *"Face Pull"*? Míralo, apréndelo, domínalo.

### ⏱️ **Modo Batalla (Live Workout)**

  - **Cronómetro en vivo:** El tiempo vuela cuando haces *Bulgarian Split Squats* (o se hace eterno, depende).
  - **Registro de Sets:** Peso, Reps, RIR... Todo a golpe de tap.
  - **Volumen Total en Tiempo Real:** Para que sepas exactamente cuántas toneladas moviste hoy (y se lo restriegues a tus amigos).

### 📈 **El Salón de los Héroes (Historial)**

  - **Gráficos de Progreso:** Muestra a tu yo del pasado quién manda ahora.
  - **Calendario de Consistencia:** Visualiza los días que fuiste una bestia y los que... bueno, nadie es perfecto.

### 🔥 **Compartir tu Leyenda**

Genera una **Tarjeta de Atleta** con tus stats para Instagram, WhatsApp o para ligar en el gym (no garantizamos resultados amorosos, pero al menos tendrás buena forma al hacer curl de bíceps).

-----

## 🛡️ Forja Reciente (Últimas Actualizaciones)

El código de AtlasPath ha sido sometido a una limpieza extrema, superando los análisis más estrictos (SonarQube 100% Clean):

  - **Desfragmentación de UI:** Complejidad cognitiva aniquilada. Todas las pantallas gigantes (`Dashboard`, `LiveWorkout`, `Nutrition`, `DetailRutina`) fueron divididas en micro-componentes altamente reutilizables.
  - **Navegación Type-Safe Blindada:** Se erradicaron los cierres forzosos (crashes) por serialización y se eliminaron los "bugs fantasma" del historial del Bottom Navigation.
  - **Lógica de Estado Cero:** Control total sobre perfiles nuevos para evitar alertas falsas (Zero-State bugs) al iniciar la app por primera vez.
  - **Perfiles Dinámicos:** Conexión en tiempo real con `DataStore` para reflejar el XP, Nivel y Rango exacto del guerrero desde el primer segundo.

-----

## 🏛️ La Forja (Arquitectura y Stack Tecnológico)

AtlasPath no se construyó con palillos y pegamento. Está forjado con **Adamantium Digital**™️.

| Capa | Descripción | Arsenal Utilizado |
| :--- | :--- | :--- |
| **🎨 Presentación** | La cara bonita y reactiva. UI Desacoplada. | **Jetpack Compose**, **MVI** (StateFlow), **Type-Safe Navigation**. |
| **🧠 Dominio** | El cerebro. Puro Kotlin. | **Use Cases**, **Repositorios** (Interfaces). |
| **🗄️ Datos** | La bóveda subterránea donde vive la verdad. | **Room** (Fuente Única de la Verdad), **Retrofit**, **Moshi**, **DataStore**. |

### 🧰 Mecánicas Especiales (Librerías Clave)

  - **Inyección de Dependencias:** `Dagger Hilt` (Para que todo esté en su sitio sin buscarlo).
  - **Asincronía:** `Coroutines` + `Flow` (Porque bloquear el hilo principal es de villanos).
  - **Imágenes:** `Coil` (Soporta GIFs como un campeón).
  - **Captura Épica:** `Capturable` (Para inmortalizar tu progreso visualmente).

-----

## 🚀 Guía de Iniciación (Instalación)

¿Listo para la aventura? Necesitarás dos artefactos mágicos (API Keys gratuitas).

### 1\. Clonar el Grimorio

```bash
git clone https://github.com/TU_USUARIO/AtlasPath.git
cd AtlasPath
```

### 2\. Conseguir las Llaves del Reino

1.  **🔑 Llave de Gemini:** Ve al oráculo de [Google AI Studio](https://aistudio.google.com/app/apikey).
2.  **🔑 Llave de la Biblioteca Perdida (ExerciseDB):** Suscríbete al plan gratuito en [RapidAPI](https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb).

### 3\. El Ritual del `local.properties`

Crea un archivo llamado **`local.properties`** en la raíz del proyecto (ese lugar oscuro donde vive `settings.gradle.kts`) y escribe el hechizo:

```properties
# Mis llaves secretas (No se suben a GitHub - ¡Ni se te ocurra!)
EXERCISE_DB_KEY=tu_llave_de_rapid_api_aqui
GEMINI_API_KEY=tu_llave_de_google_gemini_aqui
```

### 4\. Invocar a Gradle

Abre **Android Studio** (Iguana o superior). Espera a que la magia sincronice. Luego:
`Build` -\> `Clean Project` -\> `Rebuild Project`.
Presiona el botón de ▶️ **Run** y prepárate para sudar.

-----

## 👑 El Herrero (Autor)

![Logo](https://img.icons8.com/color/96/000000/barbell.png)

**Angel Paredes Almonte** *Estudiante de Ingeniería - UCNE | Levantador de Pesas y de Excepciones*

> *"Este proyecto nació en las mazmorras de la asignatura **Aplicada II** como prueba de fuego. Salió con Clean Architecture, MVI, y una dependencia emocional de las barras olímpicas."*

¿Te gusta el proyecto? **Dale una estrella ⭐ en GitHub** (no pesa nada y me da XP).

-----

> *"El hierro no miente. El código tampoco. Forja ambos."* 🛡️


<img width="1080" height="2400" alt="Screenshot_20260415_192154_AtlasPath" src="https://github.com/user-attachments/assets/eecb4039-c15a-4893-b395-612b8651d542" />
<img width="1080" height="2400" alt="Screenshot_20260415_192148_AtlasPath" src="https://github.com/user-attachments/assets/b53405ba-2f11-4ed0-a739-cb84c2f34072" />
<img width="1080" height="2400" alt="Screenshot_20260415_192150_AtlasPath" src="https://github.com/user-attachments/assets/2b790d70-0c95-4588-83ce-f4df0601a177" />
<img width="1080" height="2400" alt="Screenshot_20260415_192046_AtlasPath" src="https://github.com/user-attachments/assets/17ed811e-0e17-4a5e-8a35-0826d29437fd" />
<img width="1080" height="2400" alt="Screenshot_20260415_192145_AtlasPath" src="https://github.com/user-attachments/assets/aa70b932-6a3f-479c-a960-861362f22f9a" />
<img width="1080" height="2400" alt="Screenshot_20260415_192028_AtlasPath" src="https://github.com/user-attachments/assets/ae541d70-9221-4450-8f22-f48d2297b55b" />
