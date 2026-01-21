# Guía rápida de desarrollo (DEV_SETUP)

Requisitos
---------
- JDK 11 o superior (según configuración del proyecto).
- Android Studio con soporte para Jetpack Compose.
- Un emulador o dispositivo con Android 8+ (varias pantallas usan LocalDate/Time que requieren API 26/27+ para algunas funciones).

Comandos útiles (PowerShell)
----------------------------
Abrir PowerShell en la raíz del proyecto (`C:\Users\caram\AndroidStudioProjects\MyProyectoFinal_DIN_CarlosCarameCerero`):

```powershell
# Build completo
.\gradlew.bat clean build

# Compilar solo app debug
.\gradlew.bat :app:assembleDebug

# Ejecutar tests unitarios
.\gradlew.bat testDebugUnitTest

# Lint
.\gradlew.bat :app:lintDebug
```

Notas
-----
- Si añades nuevas dependencias, revisa `build.gradle.kts` y sincroniza el proyecto.
- Para ejecutar pruebas instrumentadas necesitas un emulador o dispositivo conectado.
- Las rutas y comandos están pensadas para PowerShell en Windows.
