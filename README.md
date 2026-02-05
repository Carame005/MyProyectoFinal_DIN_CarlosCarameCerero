# AutiCare — Aplicación (módulo app)

Este README es una guía y resumen a nivel del módulo `app` para desarrolladores, cuidadores y usuarios finales (especialmente pensado para personas con problemas de memoria / Alzheimer).

Resumen rápido
--------------
AutiCare es una aplicación de ejemplo que ayuda a gestionar tareas, eventos y colecciones de vídeo personales. Está desarrollada con Jetpack Compose y Material3 y guarda los datos localmente por usuario.

Características principales
--------------------------
- Registro e inicio de sesión de usuario.
- Pantallas principales: Inicio (resumen), Tareas, Calendario y Progreso/Vídeos.
- Persistencia local por usuario (ficheros JSON manejados por `AppRepository`).
- Reproducción de vídeos seleccionados desde la galería (permite permisos persistentes sobre URIs).
- Programación de recordatorios mediante `AlarmManager` y notificaciones.
- Menú de ajustes con opciones básicas: cambiar nombre, tema "filtro claro", notificaciones.

Documentación y manuales
------------------------
En la carpeta `app/src/main/java/.../documentacion/` encontrarás la documentación creada automáticamente:

- `ARCHITECTURE.md` — Resumen de la arquitectura y decisiones.
- `LIBRARIES.md` — Lista y justificación de librerías usadas.
- `COMPONENTS.md` — Catálogo de componentes Compose reutilizables.
- `PERSISTENCE.md` — Formatos y nombres de ficheros para datos persistentes.
- `SECURITY.md` — Riesgos detectados y recomendaciones (importante leer).
- `DEV_SETUP.md` — Requisitos y comandos para desarrollar (PowerShell).
- `TESTS.md` — Estrategia de pruebas recomendada.
- `TODO.md` — Mejoras propuestas y prioridades.
- `USER_MANUAL.md` — Manual de usuario pensado para personas con Alzheimer y cuidadores.

Cómo ejecutar y probar (PowerShell)
----------------------------------
Abrir PowerShell en la raíz del proyecto (`.../MyProyectoFinal_DIN_CarlosCarameCerero`) y ejecutar:

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

Desarrollo y estructura de código
--------------------------------
- `MainActivity.kt` — punto de entrada; monta el Theme y el `MainScaffold` con `NavController`.
- `pantallas/` — contiene las pantallas del flujo (Home, Tasks, Calendar, Videos, Login, etc.).
- `utils/` — componentes Compose reutilizables (TopBar, BottomBar, TaskCard, CollectionCard, CalendarioGrid, etc.).
- `repository/` — `AppRepository` centraliza persistencia (leer/escribir JSON por usuario).
- `model/` — data classes usadas en la app.

Notas para cuidadores y usuarios
-------------------------------
- Para usuarios con Alzheimer, hay un `USER_MANUAL.md` diseñado con lenguaje sencillo y pasos claros. Está pensado para imprimirse o leerse con un cuidador.
- Recomendación para cuidadores: mantener la contraseña en un lugar seguro y supervisar las primeras interacciones del usuario con la app.
