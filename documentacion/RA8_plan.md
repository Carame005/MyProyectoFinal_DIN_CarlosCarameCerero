RA8 — Plan de pruebas (Regresión, Volumen/estrés, Seguridad, Uso de recursos)

Resumen
-------
Este documento contiene un plan ejecutable para evaluar los criterios RA8 del proyecto Android. Incluye: casos de regresión priorizados, scripts PowerShell para estrés y recolección de métricas, checklist de seguridad, ejemplos de tests automatizables (Kotlin) y una matriz de trazabilidad.

1) Casos de regresión priorizados
---------------------------------
(Ver lista numerada en el plan: login, biometría, persistencia de sesión, imagen de perfil, calendario & alarmas, notificaciones en background, reproducción de vídeo, gestión de tutorizados, permisos de eliminación por rol, validaciones al crear vídeo, sesión/roles tras logout)

2) Plan de volumen/estrés
--------------------------
Herramientas: adb/Monkey, scripts PowerShell, Firebase Test Lab (opcional)
Comandos ejemplo (PowerShell):

- Monkey stress:

```powershell
adb shell monkey -p com.example.myproyectofinal_din_carloscaramecerero --throttle 300 -v 10000
```

- Crear eventos en bucle (requiere broadcast receiver o endpoint):

```powershell
for ($i=0; $i -lt 1000; $i++) {
  adb shell am broadcast -a com.example.app.CREATE_EVENT --es title "Test $i" --es time "$(Get-Date -Format o)"
  Start-Sleep -Milliseconds 50
}
```

Recolección de métricas: usar los scripts en documentacion/tests/collect_metrics.ps1

3) Plan de seguridad
---------------------
Checklist rápido:
- Autenticación: no almacenar contraseñas en claro; tokens en EncryptedSharedPreferences.
- Autorización: comprobar que isAdmin controla borrado; pruebas API/Mock.
- Permisos: declarar SCHEDULE_EXACT_ALARM, RECORD_AUDIO sólo si son necesarios; pedir permisos en runtime.
- Almacenamiento seguro: no guardar biometría; usar KeyStore para claves.
- Input validation: sanitizar URLs antes de cargarlas en WebView.

Herramientas: MobSF (static/dynamic), OWASP ZAP si hay backend, grep/local scans.

4) Uso de recursos
------------------
Comandos utiles (PowerShell):

```powershell
adb shell dumpsys meminfo com.example.myproyectofinal_din_carloscaramecerero > meminfo.txt
adb shell top -n 1 -b | findstr com.example.myproyectofinal_din_carloscaramecerero > cpu.txt
adb shell dumpsys batterystats --charged > batterystats.txt
```

Umbrales sugeridos: memoria < 150-200 MB en uso normal; CPU < 40% idle; jank < 100 ms; bateria consumo < 3%/h en uso típico.

5) Tests automatizables (plantillas)
------------------------------------
Incluye snippets de Unit test, Compose test y Espresso en Kotlin. (Ver secciones más abajo en el archivo)

6) CI (GitHub Actions)
----------------------
Ejemplo de workflow para ejecutar tests en emulador y unit tests. Se sugiere usar `reactivecircus/android-emulator-runner`.

7) Matriz de trazabilidad
-------------------------
Mapa RA8.c/d/e/f => tipo de prueba => evidencia esperada (tests, logs, screenshots, meminfo, perfetto).

Notas importantes detectadas en el repo
--------------------------------------
- AlarmManager: SecurityException por SCHEDULE_EXACT_ALARM. Recomiendo usar WorkManager para mayor compatibilidad o pedir permiso SCHEDULE_EXACT_ALARM.
- Dependencia `androidx.biometric:biometric-ktx:1.1.0` no encontrada en repositorios; usar `androidx.biometric:biometric:1.1.0` o ajustar repositorios a google()/mavenCentral().
- Notificaciones en background necesitan canal con importancia alta y permiso en Android 13+.
- Reproducción embebida de YouTube puede requerir ajustes en WebView (JS habilitado) o usar YouTube API/PlayerView.

Anexos
------
- Scripts PowerShell en documentacion/tests/
- Plantillas de test Kotlin (en RA8_plan) para copiar dentro de /app/src/androidTest o /app/src/test

Si quieres que cree los tests Kotlin y los ponga en /app/src/androidTest o genere los artefactos CI, indícalo y lo hago en la siguiente iteración.

