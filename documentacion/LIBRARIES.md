# Librerías y dependencias usadas (RA1)

Listado de librerías principales empleadas en el proyecto y su uso funcional.

Dependencias clave
------------------
- Jetpack Compose (`androidx.compose.*`)
  - Uso: UI declarativa, componentes y layouts.
  - Archivos relacionados: `app/src/main/java/.../utils/*.kt`, `app/src/main/java/.../pantallas/*.kt`.

- Material3 (`androidx.compose.material3`)
  - Uso: componentes y theming.
  - Archivos relacionados: `app/src/main/java/.../ui/theme/Theme.kt`, `Color.kt`, `Type.kt`.

- Navigation Compose (`androidx.navigation.compose`)
  - Uso: navegación entre pantallas (`NavController`, `NavHost`).
  - Archivos relacionados: `app/src/main/java/.../pantallas/PrincipalPantalla.kt`, `model/AppRoute.kt`.

- Coil (`coil-compose`)
  - Uso: carga de imágenes (avatar) con `AsyncImage`.
  - Evidencia en: `utils/TutorComponente.kt`, `utils/Componentes.kt`, `utils/LoginComponente.kt`.

- AndroidX Activity / Activity Result APIs
  - Uso: `rememberLauncherForActivityResult`, selección de imágenes/vídeos.
  - Archivos: `pantallas/TutorPantalla.kt`, `utils/VideosComponente.kt`, `utils/LoginComponente.kt`.

- Android core / compat / notification
  - Uso: `AlarmManager`, `NotificationCompat`, `PendingIntent` para recordatorios.
  - Archivos: `pantallas/CalendarioAlarms.kt`, `pantallas/CalendarioPantalla.kt` (`AlarmReceiver`), `pantallas/AlarmScheduler.kt`.

- Kotlin stdlib y coroutines
  - Uso: utilidades de lenguaje y concurrencia cuando procede.
  - Archivos: varias utilidades en pantallas y repositorio (`repository/appRepo.kt`).

- Biometric API (`androidx.biometric`)
  - Uso: autenticación biométrica para inicio rápido.
  - Archivos: `security/BiometricHelper.kt`, `pantallas/LoginPantalla.kt`, `utils/SettingsDrawer`.

Observaciones
-------------
- Las versiones exactas de las dependencias se gestionan en `build.gradle.kts` (raíz y `app/`) o en `gradle/libs.versions.toml`.
- Para análisis de calidad y seguridad de código pueden usarse herramientas adicionales como `Detekt`, `ktlint` y `SpotBugs` (no integradas en el repositorio actual por defecto).

Recomendación para RA1.a
------------------------
- Añadir una breve justificación por cada dependencia (por qué se eligió) en este fichero ayuda a cumplir RA1.a. Por ejemplo: Coil para manejo eficiente de images en Compose, BiometricPrompt para delegar seguridad al sistema, Navigation Compose para mantener estado y backstack de forma coherente.
- Si se requiere, puedo ampliar este fichero con los números de versión y el fragmento `dependencies { ... }` extraído de `app/build.gradle.kts`.
