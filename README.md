# TutorOrganiza — Módulo app (introducción y guía rápida)

Este README es una introducción de alto nivel al módulo `app` del proyecto TutorOrganiza. Su objetivo es ayudar a desarrolladores y revisores a encontrar rápidamente las partes relevantes del proyecto (código, documentación y tests). La documentación detallada y las guías de uso están en la carpeta `documentacion` en la raíz del repositorio.

Resumen rápido
--------------
TutorOrganiza es una aplicación desarrollada con Jetpack Compose y Material3 pensada para ayudar a gestionar tareas, eventos y colecciones de vídeo, con especial foco en personas que necesitan que un tutor les lleve una organización diaria (usuarios asistidos) y en los tutores/cuidadores que los gestionan. Este módulo contiene la implementación de la app y la documentación técnica y de usuario.

Estructura principal (dónde buscar)
-----------------------------------
- Código fuente (pantallas, componentes, repositorio y modelos):
  - `app/src/main/java/.../pantallas/`  — Todas las pantallas (Login, Home, Tareas, Calendario, Videos, Tutor, etc.).
  - `app/src/main/java/.../utils/`      — Componentes Compose reutilizables (cards, barras, ajustes, etc.).
  - `app/src/main/java/.../model/`      — Data classes y enums (`User`, `Task`, `CalendarEvent`, `TaskStatus`, ...).
  - `app/src/main/java/.../repository/` — `AppRepository` (persistencia en ficheros JSON por usuario).
  - `app/src/main/java/.../security/`   — Helpers de seguridad (p.ej. `BiometricHelper`).
  - `app/src/main/java/.../receivers/`  — Broadcast receivers (p.ej. `BootReceiver`).

- Documentación (guías, decisiones y manuales):
  - `documentacion/` contiene los MD con documentación de distintos aspectos de la app:
    - `ARCHITECTURE.md`, `LIBRARIES.md`, `COMPONENTS.md`, `PERSISTENCE.md`, `SECURITY.md`, `DEV_SETUP.md`, `TESTS.md`, `USER_MANUAL.md`, `INFORMES.MD`, `NUI.md`, etc.
    - `capturas/` — capturas y evidencias (por ejemplo, pruebas y pantallas).

- Tests (unitarios / robolectric / utilidades):
  - [Tests](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/test/kotlin/com/example/tests) — tests unitarios actuales (por ejemplo `AppRepositoryTest.kt`, `CalendarioAlarmsTest.kt`, `VolumeTests.kt`, ...).
  - Informes de ejecución y reportes se generan en `app/build/reports/tests/` y resultados XML en `app/build/test-results/`.

Puntos importantes y notas rápidas
---------------------------------
- Persistencia: el proyecto usa ficheros JSON por usuario gestionados por `AppRepository`. Revisa `documentacion/PERSISTENCE.md` para detalles sobre rutas y formatos.
- Notificaciones y AlarmManager: la app programa alarmas para recordatorios; en Android modernos puede requerir permisos especiales (`SCHEDULE_EXACT_ALARM`) o manejo por canal. Revisa `pantallas/CalendarioAlarms.kt` (o el archivo de alarms correspondiente) y `documentacion/TESTS.md` para pruebas relacionadas.
- Biometría y NUI: existe un helper `security/BiometricHelper.kt` y un documento `documentacion/NUI.md` que comenta ideas para interfaces naturales (biometría, voz, gestos) orientadas al nuevo público objetivo (login rápido y accesibilidad para usuarios asistidos).

Comandos básicos (PowerShell)
-----------------------------
Abrir PowerShell en la raíz del proyecto y ejecutar:

```powershell
# Compilar y construir
.\gradlew.bat clean build

# Generar APK debug
.\gradlew.bat :app:assembleDebug

# Ejecutar tests unitarios
.\gradlew.bat testDebugUnitTest

# Ejecutar lint (si está configurado)
.\gradlew.bat :app:lintDebug
```

Dónde mirar si algo falla
-------------------------
- Build fallido: revisar la salida de Gradle en la ventana de ejecución de Android Studio y el fichero `gradle.properties` / `build.gradle.kts`.
- Tests fallando: `app/build/reports/tests/testDebugUnitTest/index.html` contiene el informe HTML con fallos y trazas.
- Logs en ejecución: usar Logcat y filtrar por el `package` de la app.
