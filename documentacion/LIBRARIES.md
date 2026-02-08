# Librerías y dependencias usadas (RA1)

Listado de librerías principales empleadas en el proyecto y su uso funcional.

Dependencias clave
------------------
- Jetpack Compose (`androidx.compose.*`)
  - Uso: UI declarativa, componentes y layouts.

- Material3 (`androidx.compose.material3`)
  - Uso: componentes y theming.

- Navigation Compose (`androidx.navigation.compose`)
  - Uso: navegación entre pantallas (`NavController`, `NavHost`).

- Coil (`coil-compose`)
  - Uso: carga de imágenes (avatar) con `AsyncImage`.

- AndroidX Activity / Activity Result APIs
  - Uso: `rememberLauncherForActivityResult`, selección de imágenes/vídeos.

- Android core / compat / notification
  - Uso: `AlarmManager`, `NotificationCompat`, `PendingIntent` para recordatorios.

- Kotlin stdlib y coroutines
  - Uso: utilidades de lenguaje y concurrencia cuando procede.

- Biometric API (`androidx.biometric`)
  - Uso: autenticación biométrica para inicio rápido.

Observaciones
-------------
- Las versiones exactas de las dependencias se gestionan en `build.gradle.kts` (raíz y `app/`) o en `gradle/libs.versions.toml`.
- Para análisis de calidad y seguridad de código pueden usarse herramientas adicionales como `Detekt`, `ktlint` y `SpotBugs` (no integradas en el repositorio actual por defecto).
