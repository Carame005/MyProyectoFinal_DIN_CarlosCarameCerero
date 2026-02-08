# Arquitectura del proyecto

Resumen
-------
La aplicación está desarrollada con Jetpack Compose (UI declarativa) y Material3. La navegación está gestionada por `NavController` de `androidx.navigation.compose`. La lógica de persistencia local está centralizada en `AppRepository` (archivo: `repository/appRepo.kt`), que guarda y carga datos en ficheros internos de la app (`Context.openFileOutput` / `openFileInput`) usando JSON.

Capas principales
-----------------
- UI (Compose): `pantallas/` y `utils/` contienen las pantallas y componentes.
- Dominio / Modelos: `model/` con data classes (`User`, `Task`, `CalendarEvent`, etc.).
- Persistencia: `repository/AppRepository` maneja la lectura/escritura de datos por usuario.
- Seguridad: `security/BiometricUtils.kt` gestiona autenticación biométrica.
- Notificaciones: `receivers/NotificationReceiver.kt` para gestionar alarmas y notificaciones.
- Activity: `MainActivity` monta el theme y el `MainScaffold` con navegación.

Modelo user-admin y acceso tutor
-------------------------------
La app soporta dos tipos de usuarios: normales (usuarios asistidos) y administradores (tutores). Los tutores pueden ver y gestionar datos de los usuarios a su cargo. En la versión documentada la gestión de acceso se basa en el rol (`isAdmin`) y en políticas que determinan qué acciones son permitidas para cada rol (p. ej. tutorizados no pueden eliminar contenidos asignados por un tutor).

Las decisiones de diseño se orientan a facilitar que un tutor organice el día del usuario asistido: asignar tareas, programar eventos con recordatorios, y crear colecciones de material audiovisual instructivo.

Decisiones y justificación
---------------------------
- Jetpack Compose + Material3: elección por productividad, componibilidad y flexibilidad en el diseño UI.
- Persistencia en ficheros JSON por usuario: solución sencilla y adecuada para un prototipo educativo sin backend.
- Uso de `AlarmManager` + `BroadcastReceiver` para notificaciones programadas (Calendario): diseño que permite programación local de recordatorios.

Propuestas de mejora (escala / producción)
------------------------------------------
- Migración a Room para consultas complejas y relaciones entre entidades.
- Separación de la lógica de persistencia en interfaces (Repository pattern) para facilitar pruebas y migraciones.
- Migración de credenciales a almacenamiento seguro (`EncryptedSharedPreferences` / Android Keystore).

Estructura de carpetas (relevante)
----------------------------------
- `app/src/main/java/.../pantallas/` — pantallas y flows.
- `app/src/main/java/.../utils/` — componentes Compose reutilizables.
- `app/src/main/java/.../repository/` — persistencia local.
- `app/src/main/java/.../model/` — data classes.
- `app/src/main/java/.../documentacion/` — documentación (esta carpeta).
- `app/src/main/java/.../receivers/` — BroadcastReceiver para notificaciones.
- `app/src/main/java/.../security/` — utilidades de seguridad (Biometria).
