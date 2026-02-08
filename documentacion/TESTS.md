# Pruebas relacionadas con RA8

Este documento describe las pruebas existentes en el proyecto relacionadas con los criterios RA8 (Pruebas avanzadas) y su mapeo frente a la rúbrica. Incluye:
- Tests implementados actualmente (ubicación y objetivo).
- Instrucciones de ejecución.
- Cobertura actual frente a RA8.
- Huecos detectados.

Resumen
-------
Se han añadido tests unitarios de JVM (Robolectric) que cubren casos básicos de regresión en persistencia y comportamiento del scheduler de alarmas, así como pruebas específicas para seguridad y volumen.

Tests implementados (ubicación y objetivo)
------------------------------------------
- `app/src/test/kotlin/com/example/tests/AppRepositoryTest.kt` — pruebas sobre `AppRepository` (guardar/leer usuario, tareas, eventos; limpiar datos; gestión de tutorizados).
- `app/src/test/kotlin/com/example/tests/CalendarioAlarmsTest.kt` — pruebas sobre funciones de `CalendarioAlarms` (`ensureNotificationChannel`, `scheduleAlarm`, `cancelAlarm`) usando `ShadowAlarmManager` de Robolectric.
- `app/src/test/kotlin/com/example/tests/CalendarioAlarmsSecurityTest.kt` — prueba que simula denegación de permiso (`SecurityException`) para `setExactAndAllowWhileIdle` y verifica el fallback a `set(...)`.
- `app/src/test/kotlin/com/example/tests/VolumeTests.kt` — pruebas de volumen que guardan 500 eventos y 500 tareas para medir tiempo y tamaño de fichero (prueba básica de estrés/usabilidad de persistencia).

Evidencia de ejecución
---------------------
- Informe HTML generado por Gradle: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Captura de salida de build/tests: `documentacion/capturas/TestSalida.png`.

Cómo ejecutar los tests
-----------------------
Desde la raíz del proyecto ejecutar (PowerShell):

```powershell
# Ejecutar todos los tests unitarios del módulo app
.\gradlew.bat :app:testDebugUnitTest --info

# Ejecutar solo los tests del paquete de pruebas añadidas
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.*" --info

# Ejecutar un único test (ej. security)
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.CalendarioAlarmsSecurityTest" --info

# Ejecutar el test de volumen
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.VolumeTests" --info
```

Notas técnicas y recientes modificaciones
----------------------------------------
- Se incorporó la abstracción `AlarmScheduler` y su implementación `RealAlarmScheduler` para permitir inyección en tests y simular `SecurityException` en entornos de prueba.
- El test de seguridad usa un `FakeScheduler` que lanza `SecurityException` en `setExactAndAllowWhileIdle` y verifica que el código realiza el fallback a `set(...)`.
- El test de volumen utiliza el `AppRepository` real y escribe a los ficheros privados simulados por Robolectric.

Mapeo frente a la rúbrica RA8 (estado actual)
---------------------------------------------
- RA8.c Pruebas de regresión: cubierto a nivel básico por `AppRepositoryTest` y tests de calendario. Puntuación estimada: 0.5–1.

- RA8.d Pruebas de volumen/estrés: parcialmente cubierto por `VolumeTests` (500 elementos). Para alcanzar puntuación máxima conviene ampliar a >1000 elementos y añadir métricas automatizadas en CI. Puntuación estimada: 0.5–1.

- RA8.e Pruebas de seguridad: cubierto por `CalendarioAlarmsSecurityTest`. Puntuación estimada: 1–1.5.

- RA8.f Uso de recursos: parcialmente cubierto por `VolumeTests` (tamaño de fichero y tiempo). Requiere ampliación para mediciones de memoria y pruebas de leak para alcanzar máxima puntuación. Puntuación estimada: 0.5–1.

Huecos identificados
---------------------
- Tests instrumentados (Android) para verificar generación de ficheros y notificaciones en emuladores reales.
- Pruebas de regresión ampliadas con dataset mayor (>1000 registros) y aserciones de rendimiento.
- Integración de pruebas en CI con límites aceptables de tiempo/recursos.

Detalle de tests por fichero (resumen)
--------------------------------------
- `AppRepositoryTest.kt`: valida roundtrips de serialización/deserialización, `clearAllData` y gestión de tutorizados.
- `CalendarioAlarmsTest.kt`: valida creación de canales y scheduling/cancelación de alarmas con `ShadowAlarmManager`.
- `CalendarioAlarmsSecurityTest.kt`: valida manejo de `SecurityException` y fallback.
- `VolumeTests.kt`: valida persistencia ante volumen moderado de datos y mide tiempo/tamaño.

Evidencia RA8
---------------------------------
- Capturas en `documentacion/capturas/` de build succeesful.
- Resultados de tests en `app/build/reports/tests/` y `app/build/test-results/`.