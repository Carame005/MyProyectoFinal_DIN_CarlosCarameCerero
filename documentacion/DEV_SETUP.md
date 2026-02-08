# Guía rápida de desarrollo (RA7)

Requisitos
---------
- JDK 17 recomendado (aunque el proyecto compila con jvmTarget 11, Robolectric y herramientas de test funcionan mejor con JDK 17).
- Android Studio con soporte para Jetpack Compose.
- Android SDK (Platform 36) y build-tools compatibles con `compileSdk = 36`.
- Un emulador o dispositivo con Android 8+ (varias pantallas usan LocalDate/Time que requieren API 26/27+ para algunas funciones).

Configurar la sesión de PowerShell para usar JDK 17 (temporal)
------------------------------------------------------------
Si se dispone de JDK 17 instalado (por ejemplo en `C:\Program Files\Java\jdk-17`), en PowerShell se puede usar sólo en la sesión actual:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:PATH = "${env:JAVA_HOME}\bin;${env:PATH}"
java -version
javac -version
```

Comandos útiles (PowerShell)
----------------------------
Abrir PowerShell en la raíz del proyecto (`C:\Users\caram\AndroidStudioProjects\MyProyectoFinal_DIN_CarlosCarameCerero`):

```powershell
# Build completo (limpio + compila)
.\gradlew.bat clean build

# Generar debug APK
.\gradlew.bat :app:assembleDebug

# Generar release APK y AAB (firma se configura aparte)
.\gradlew.bat :app:assembleRelease
.\gradlew.bat :app:bundleRelease

# Ejecutar tests unitarios (Robolectric) del módulo app
.\gradlew.bat :app:testDebugUnitTest --info

# Ejecutar pruebas instrumentadas en dispositivo/emulador conectado
.\gradlew.bat :app:connectedDebugAndroidTest --info

# Lint
.\gradlew.bat :app:lintDebug

# Forzar refresco de dependencias
.\gradlew.bat --refresh-dependencies
```

Notas sobre los comandos
- La tarea para ejecutar los tests unitarios del módulo `app` es `:app:testDebugUnitTest`.
- Para builds reproducibles en CI conviene usar `bundleRelease` y firmar el AAB con un keystore seguro en los secretos del runner.

SDK, AVD y emuladores (línea de comandos)
----------------------------------------
Si se usan las command-line tools (sdkmanager/avdmanager) se pueden instalar plataformas y crear AVDs:

```powershell
# Instalar componentes necesarios (ejemplo)
sdkmanager "platform-tools" "platforms;android-36" "build-tools;36.0.0" "system-images;android-33;google_apis;x86"

# Crear un AVD (ejemplo API 33)
avdmanager create avd -n test_api_33 -k "system-images;android-33;google_apis;x86" --device "pixel"

# Iniciar AVD (o abrirlo desde Android Studio)
emulator -avd test_api_33
```

Local.properties y ruta del SDK
-------------------------------
Asegurarse de que `local.properties` apunte al SDK local (archivo no debe subirse al repositorio):

```
sdk.dir=C\:\Users\<tu-usuario>\AppData\Local\Android\Sdk
```

ADB (instalación y desinstalación rápida)
-----------------------------------------
```powershell
# Instalar APK en un dispositivo/emulador conectado
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Desinstalar (packageName con tu applicationId)
adb uninstall com.example.myproyectofinal_din_carloscaramecerero
```

Consejos para Robolectric y JDK
-------------------------------
- Si aparecen errores de tipo "Unsupported class file major version" o problemas al instrumentar clases, ejecutar Gradle con JDK 17.
- Robolectric suele requerir que el test runner y la versión de SDK sean compatibles; en los tests actuales se fuerza `@Config(sdk = [33])` cuando es necesario.

Notas CI / Firma
----------------
- No subir el keystore al repositorio. Para publicar en Play Store configurar `signingConfigs` en Gradle y usar secretos en CI (GitHub Actions / GitLab CI) para inyectar el keystore y las contraseñas.
- Flujo sugerido en CI: checkout → setup Java 17 and Android SDK → `./gradlew :app:bundleRelease` → subir `app-release.aab`.

Problemas frecuentes y soluciones rápidas
----------------------------------------
- Robolectric falla por versión de JDK: usar JDK 17 en la sesión.
- Error por dependencia no encontrada: comprobar `google()` y `mavenCentral()` en `settings.gradle.kts`.
- Instrumented tests fallan por no haber emulador: ejecutar `emulator -avd <name>` y comprobar `adb devices` antes.

Distribución (RA7) — empaquetado, firma y publicación
----------------------------------------------------
Esta sección describe los pasos y resultados esperados para cumplir los criterios del RA7 (empaquetado, instaladores, firma, canales y despliegue).

RA7.a — Empaquetado de la aplicación
- Generar APK (debug/release) o AAB (Android App Bundle) desde Gradle:
  - Debug APK:

```powershell
.\gradlew.bat :app:assembleDebug
```

  - Release APK (requiere configuración de firma):

```powershell
.\gradlew.bat :app:assembleRelease
```

  - App Bundle (recomendado para Play Store):

```powershell
.\gradlew.bat :app:bundleRelease
```

- Resultados:
  - APK: `app/build/outputs/apk/release/app-release.apk`
  - AAB: `app/build/outputs/bundle/release/app-release.aab`

RA7.b — Personalización del instalador
- Elementos a comprobar/ajustar antes de generar release:
  - `applicationId` en `app/build.gradle.kts` (identificador del paquete).
  - `versionCode` y `versionName`.
  - Iconos (res/mipmap), nombre (strings.xml) y splash si aplica.
- Actualizar `AndroidManifest` si se añaden permisos o metadata que alteren la instalación.

RA7.c — Paquete desde el entorno / CI
- Automatizar generación en CI (GitHub Actions / GitLab CI) y subir artefactos.
- Flujo sugerido en CI: Checkout → Setup Java 17 + Android SDK → `./gradlew :app:bundleRelease` → subir artefacto / publicar.

RA7.d — Herramientas externas
- Herramientas identificadas: Fastlane, Google Play Console API, Firebase App Distribution, bundletool.

RA7.e — Firma digital (keystore)
- Resumen: generar keystore con `keytool` y configurar `signingConfigs` en Gradle.

RA7.f — Instalación desatendida / despliegue masivo
- Nota: la instalación silenciosa requiere MDM/EMM y no está soportada en dispositivos de usuarios sin privilegios.

RA7.g — Desinstalación y limpieza
- `adb uninstall <package>` para desarrollo.
- Para purgar datos de usuario en la app usar `AppRepository.clearUserData(ctx, userEmail)`.

RA7.h — Canales de distribución
- Canales identificados: Google Play (production/internal), Firebase App Distribution, GitHub Releases.

Checklist mínimo para un dev nuevo
----------------------------------
- [ ] Instalar JDK 17 y configurar `JAVA_HOME` en la sesión.
- [ ] Instalar Android SDK (platform-tools, platforms;android-36, build-tools apropiados).
- [ ] Añadir `sdk.dir` en `local.properties` apuntando al SDK.
- [ ] Crear y arrancar un AVD API >= 26 para pruebas instrumentadas.
- [ ] Ejecutar `.\gradlew.bat clean build` para verificar configuración.
