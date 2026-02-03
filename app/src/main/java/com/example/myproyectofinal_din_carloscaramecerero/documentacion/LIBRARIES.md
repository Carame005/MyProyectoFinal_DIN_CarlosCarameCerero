# Librerías y dependencias usadas(RA1)

En el código se utilizan principalmente las siguientes librerías del ecosistema Android (algunas vienen del SDK o AndroidX):

- Jetpack Compose (androidx.compose.*)
  - Uso: UI declarativa, componentes, layouts.
  - Justificación: Productividad y flexibilidad en el diseño UI.

- Material3 (androidx.compose.material3)
  - Uso: Componentes y theming moderno.
  - Justificación: Consistencia visual y soporte de tokens de diseño.

- Navigation Compose (androidx.navigation.compose)
  - Uso: Navegación entre pantallas (`NavController`, `NavHost`).

- Coil (coil-compose)
  - Uso: Carga de imágenes (avatar) con `AsyncImage`.

- AndroidX Activity / Activity Result APIs
  - Uso: `rememberLauncherForActivityResult`, selección de imágenes/vídeos.

- Android core / compat / notification
  - Uso: AlarmManager, NotificationCompat, PendingIntent para recordatorios.

- Kotlin stdlib y coroutines (si se usan en otras partes)

- Biometric API (androidx.biometric)
  - Uso: Autenticación biométrica para login seguro.

Notas
-----
- La versión exacta de cada dependencia se gestiona en `build.gradle.kts` en el proyecto raíz o en `app/`. Recomendación: documentar las versiones en `LIBRARIES.md` o en `gradle/libs.versions.toml` para auditoría.
- Si se requiere añadir análisis de seguridad, se pueden incorporar herramientas como `Detekt`, `KTlint`, `SpotBugs` y `MobSF` para análisis de móviles.
