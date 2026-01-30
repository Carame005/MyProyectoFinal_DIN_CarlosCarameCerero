# Arquitectura del proyecto

Resumen
-------
La aplicación está desarrollada con Jetpack Compose (UI declarativa) y Material3. La navegación está gestionada por `NavController` de `androidx.navigation.compose`. La lógica de persistencia local está centralizada en `AppRepository` (archivo: `repository/appRepo.kt`), que guarda y carga datos en ficheros internos del app (`Context.openFileOutput` / `openFileInput`) usando JSON.

Capas principales
-----------------
- UI (Compose): `pantallas/` y `utils/` contienen las pantallas y componentes.
- Dominio / Modelos: `model/` con data classes (`User`, `Task`, `CalendarEvent`, etc.).
- Persistencia: `repository/AppRepository` maneja la lectura/escritura de datos por usuario.
- Seguridad: `security/BiometricUtils.kt` gestiona autenticación biométrica.
- Notificaciones: `receivers/NotificationReceiver.kt` para gestionar alarmas y notificaciones
- Activity: `MainActivity` monta el theme y el `MainScaffold` con navegación.

Decisiones y justificación
---------------------------
- Jetpack Compose + Material3: permite UI moderna y componentes reutilizables.
- Persistencia en ficheros JSON por usuario: sencillo para el ámbito educativo/prototipo sin backend.
- Uso de `AlarmManager` + `BroadcastReceiver` para notificaciones programadas (Calendario).

Consideraciones de escalado
---------------------------
- Para producción se recomienda migrar la persistencia a una base de datos (Room) y mover credenciales a almacenamiento seguro (EncryptedSharedPreferences / Android Keystore).
- Separar lógica de persistencia en interfaces para facilitar pruebas y migraciones.

Estructura de carpetas (relevante)
----------------------------------
- `app/src/main/java/.../pantallas/` — pantallas y flows.
- `app/src/main/java/.../utils/` — componentes Compose reutilizables.
- `app/src/main/java/.../repository/` — persistencia local.
- `app/src/main/java/.../model/` — data classes.
- `app/src/main/java/.../documentacion/` — documentación (esta carpeta).
- `app/src/main/java/.../receivers/` — BroadcastReceiver para notificaciones.
- `app/src/main/java/.../security/` — utilidades de seguridad (Biometria).
