# Guía rápida de desarrollo (RA7)

Requisitos
---------
- JDK 17 recomendado (aunque el proyecto compila con jvmTarget 11, Robolectric y herramientas de test funcionan mejor con JDK 17).
- Android Studio con soporte para Jetpack Compose.
- Android SDK (Platform 36) y build-tools compatibles con `compileSdk = 36`.
- Un emulador o dispositivo con Android 8+ (varias pantallas usan LocalDate/Time que requieren API 26/27+ para algunas funciones).

Configurar la sesión de PowerShell para usar JDK 17 (temporal)
------------------------------------------------------------
Si tienes JDK 17 instalado (por ejemplo en `C:\Program Files\Java\jdk-17`), en PowerShell puedes usarlo sólo en la sesión actual:

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
- La tarea correcta para ejecutar los tests unitarios del módulo `app` es `:app:testDebugUnitTest` (no `testDebugUnitTest` a secas desde la raíz). Usar `--info` para más detalle.
- Para builds reproducibles en CI conviene usar `bundleRelease` y firmar el AAB con un keystore seguro en los secretos del runner.

SDK, AVD y emuladores (línea de comandos)
----------------------------------------
Si usas las command-line tools (sdkmanager/avdmanager) puedes instalar plataformas y crear AVDs:

```powershell
# Instalar componentes necesarios (ejemplo)
sdkmanager "platform-tools" "platforms;android-36" "build-tools;36.0.0" "system-images;android-33;google_apis;x86"

# Crear un AVD (ejemplo API 33)
avdmanager create avd -n test_api_33 -k "system-images;android-33;google_apis;x86" --device "pixel"

# Iniciar AVD (o abrirlo desde Android Studio)
emulator -avd test_api_33
```

Local.properties y ruta del SDK
--------------------------------
Asegúrate de que `local.properties` apunte a tu SDK local (solo en tu máquina, no subir al repo):

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
- Si ves errores de tipo "Unsupported class file major version" o problemas al instrumentar clases, prueba ejecutar Gradle con JDK 17 como se muestra arriba.
- Robolectric suele requerir que el test runner y la versión de SDK sean compatibles; en los tests actuales se fuerza `@Config(sdk = [33])` cuando es necesario.

Notas CI / Firma
----------------
- No subas tu keystore al repositorio. Para publicar en Play Store configura `signingConfigs` en Gradle y usa secretos en tu CI (GitHub Actions / GitLab CI) para inyectar el keystore y las contraseñas.
- Ejemplo rápido: en CI ejecuta `./gradlew :app:bundleRelease` y sube `app/build/outputs/bundle/release/app-release.aab` a Play Console.

Problemas frecuentes y soluciones rápidas
----------------------------------------
- Robolectric falla por versión de JDK: usar JDK 17 en la sesión.
- Error por dependencia no encontrada: comprobar `google()` y `mavenCentral()` en `settings.gradle.kts`.
- Instrumented tests fallan por no haber emulador: ejecutar `emulator -avd <name>` y comprobar `adb devices` antes.

Distribución (RA7) — empaquetado, firma y publicación
----------------------------------------------------
Esta sección complementa el flujo de desarrollo con pasos y buenas prácticas para cumplir los criterios del RA7 (empaquetado, instaladores, firma, canales y despliegue).

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
- No olvides actualizar el `AndroidManifest` si añades permisos o metadata que alteren la instalación.

RA7.c — Paquete desde el entorno / CI
- Recomendación: automatizar generación en CI (GitHub Actions / GitLab CI).
- Flujo sugerido en CI:
  1. Checkout.
  2. Setup Java 17 and Android SDK.
  3. `./gradlew :app:bundleRelease` (o `assembleRelease`).
  4. Subir `app-release.aab` como artefacto y/o publicar en Play Console (internal track) usando `fastlane` o Play Developer API.

RA7.d — Herramientas externas
- Herramientas recomendadas:
  - Fastlane: automatiza builds, screenshots y subida a Play.
  - Google Play Console / Play Publisher API: publicar en tracks (internal, closed, open, production).
  - Firebase App Distribution: distribuir betas a testers.
  - bundletool: para generar apks desde un AAB si lo necesitas localmente.

RA7.e — Firma digital (keystore)
- Generar un keystore (ejemplo):

```powershell
keytool -genkeypair -v -keystore my-release-key.jks -alias mykey -keyalg RSA -keysize 2048 -validity 9125
```

- Ejemplo (simplificado) `signingConfigs` en `app/build.gradle.kts` (Kotlin DSL):

```kotlin
android {
  // ...existing code...
  signingConfigs {
    create("release") {
      storeFile = file("/path/to/my-release-key.jks")
      storePassword = System.getenv("KEYSTORE_PASSWORD")
      keyAlias = System.getenv("KEY_ALIAS")
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }
  buildTypes {
    release {
      signingConfig = signingConfigs.getByName("release")
      isMinifyEnabled = false
      // proguard files etc.
    }
  }
}
```

- Buenas prácticas:
  - No subir el keystore al repo.
  - Guardar keystore y contraseñas como secretos en CI (GitHub Secrets).
  - Mantener una copia segura del keystore (necesario para actualizaciones en Play).

RA7.f — Instalación desatendida / despliegue masivo
- Instalación local (desarrollo): `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
- Instalación silenciosa/desatendida en dispositivos gestionados: requiere MDM/EMM (Device Owner) o soluciones corporativas; documentar proveedor (e.g., Microsoft Intune, MobileIron, Scalefusion).
- Nota: Android no permite instalaciones silenciosas en dispositivos de usuarios sin privilegios especiales por motivos de seguridad.

RA7.g — Desinstalación y limpieza
- Comando `adb uninstall <package>` para desarrollo:

```powershell
adb uninstall com.example.myproyectofinal_din_carloscaramecerero
```

- Para limpieza de datos del usuario en la app usar `AppRepository.clearUserData(ctx, userEmail)` (función disponible en el repo).
- Documenta en la guía de soporte los pasos para restaurar un dispositivo y purgar datos si es necesario.

RA7.h — Canales de distribución
- Canales comunes y recomendaciones:
  - Google Play (production/internal/closed): proceso estándar para apps públicas.
  - Firebase App Distribution: ideal para betas y testers rápidos.
  - GitHub Releases: publicar APK/AAB para descarga manual.
  - F-Droid: solo si el proyecto es Open Source y cumple requisitos.
- Documentar cuál canal usar y el procedimiento (en `DEV_SETUP.md` o `RELEASE.md`). .

Checklist mínimo para un dev nuevo
----------------------------------
- [ ] Instalar JDK 17 y configurar `JAVA_HOME` en la sesión.
- [ ] Instalar Android SDK (platform-tools, platforms;android-36, build-tools apropiados).
- [ ] Añadir `sdk.dir` en `local.properties` apuntando al SDK.
- [ ] Crear y arrancar un AVD API >= 26 para pruebas instrumentadas.
- [ ] Ejecutar `.\gradlew.bat clean build` para verificar configuración.
