# Pruebas relacionadas con RA8

Este documento describe las pruebas existentes en el proyecto relacionadas con los criterios RA8 (Pruebas avanzadas) y cómo se mapean contra la rúbrica. Incluye:
- qué tests están implementados actualmente (ubicación y objetivo),
- cómo ejecutarlos localmente,
- cobertura frente a los ítems RA8 (cambios/regresión, volumen/estrés, seguridad, uso de recursos),
- huecos detectados y recomendaciones para completar la evaluación RA8.

---

## Resumen rápido

Se han añadido tests unitarios de JVM (Robolectric) que cubren casos básicos de regresión en persistencia y comportamiento del scheduler de alarmas, así como pruebas específicas para seguridad y volumen:

- `app/src/test/kotlin/com/example/tests/AppRepositoryTest.kt` — pruebas sobre `AppRepository` (guardar/leer usuario, tareas, eventos; limpiar datos; gestión de tutorizados).
- `app/src/test/kotlin/com/example/tests/CalendarioAlarmsTest.kt` — pruebas sobre funciones de `CalendarioAlarms` (`ensureNotificationChannel`, `scheduleAlarm`, `cancelAlarm`) usando `ShadowAlarmManager` de Robolectric.
- `app/src/test/kotlin/com/example/tests/CalendarioAlarmsSecurityTest.kt` — prueba que simula denegación de permiso (lanza `SecurityException`) para `setExactAndAllowWhileIdle` y verifica el fallback a `set(...)`.
- `app/src/test/kotlin/com/example/tests/VolumeTests.kt` — pruebas de volumen que guardan 500 eventos y 500 tareas para medir tiempo y tamaño de fichero (prueba básica de estrés/usabilidad de persistencia).

Estos tests sirven como base para RA8.c (pruebas de regresión) y cubren RA8.e (seguridad) y RA8.d/RA8.f (volumen/uso de recursos) a nivel básico; en secciones laterales se indican huecos restantes y recomendaciones.

---

## Cambios recientes y evidencia

- Se añadió la abstracción `AlarmScheduler` y su implementación real `RealAlarmScheduler` para permitir inyección en tests y simular `SecurityException` en entornos de prueba.
- Se añadieron dos tests nuevos importantes para RA8:
  - `CalendarioAlarmsSecurityTest` (seguridad / manejo de excepciones).
  - `VolumeTests` (volumen / medida de persistencia).

Evidencia incluida en el repo:
- Captura del resultado `BUILD SUCCESSFUL` tras ejecutar los tests: `documentacion/capturas/TestSalida.png` (imagen añadida por el autor del proyecto).
- Informe HTML generado por Gradle: `app/build/reports/tests/testDebugUnitTest/index.html` (puedes abrirlo localmente para ver resultados detallados).

---

## Cómo ejecutar los tests (localmente)

Desde la raíz del proyecto (donde está `gradlew.bat`) ejecuta:

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

Notas:
- Si usas Android Studio y tienes problemas con Robolectric, asegúrate de usar JDK 17 como Gradle JDK (File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK).
- La primera ejecución descargará dependencias (Robolectric, androidx test) si no están presentes.

---

## Resultado de la ejecución (ejemplo)

Salida local proporcionada (capturada en `documentacion/capturas/TestSalida.png`):

```
BUILD SUCCESSFUL in 23s
29 actionable tasks: 3 executed, 26 up-to-date
Watched directory hierarchies: [C:\Users\caram\AndroidStudioProjects\MyProyectoFinal_DIN_CarlosCarameCerero]
```

El informe HTML con el detalle de cada test está en `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## Mapeo frente a la rúbrica RA8 (estado actualizado)

- RA8.c Pruebas de regresión: cubierto (nivel básico). Los tests unitarios detectan regresiones en la serialización/deserialización y en la lógica del calendario: puntuación esperada 0.5–1.

- RA8.d Pruebas de volumen/estrés: parcialmente cubierto. Se añadió una prueba de volumen (500 eventos y 500 tareas) que valida que la persistencia funciona y mide tiempo/tamaño; para alcanzar puntuación máxima conviene ampliar a 1000+ registros y añadir métricas de rendimiento en CI. Puntuación actual estimada: 0.5–1.

- RA8.e Pruebas de seguridad: cubierto (prueba que simula `SecurityException` y valida el fallback). Puntuación actual estimada: 1–1.5.

- RA8.f Uso de recursos: parcialmente cubierto. `VolumeTests` mide tamaño de fichero y tiempo de escritura; para máxima puntuación conviene integrar medidas de memoria y pruebas de leak en ejecuciones prolongadas. Puntuación actual estimada: 0.5–1.

---

## Huecos detectados y recomendaciones (priorizadas)

1. Subir la prueba de volumen a 1000–2000 ítems y añadir umbrales aceptables (tiempo y tamaño) en las aserciones automáticas.
2. Añadir tests que simulen condiciones adversas (I/O fallido, espacio bajo en disco, permisos denegados) para robustecer RA8.e y RA8.f.
3. Automatizar la ejecución en CI (p. ej. GitHub Actions) y publicar los informes HTML como artefactos para evidencia permanente.
4. Añadir pruebas instrumentadas (androidTest) para comprobar la entrega real de notificaciones cuando sea necesario.

---

## Implementación técnica y notas de testabilidad

- Para facilitar pruebas, se creó la interfaz `AlarmScheduler` y se refactorizó `scheduleAlarm` para aceptar una implementación inyectada; en producción sigue usándose `RealAlarmScheduler` por defecto.
- El test de seguridad usa un `FakeScheduler` que lanza `SecurityException` en `setExactAndAllowWhileIdle` y permite verificar que el código realiza el fallback a `set(...)`.
- El test de volumen utiliza el `AppRepository` real y escribe a los ficheros privados de la app; por rapidez en CI puedes sustituirlo por un backend en memoria o reducir el número de elementos.

---

## Evidencias recomendadas para la entrega RA8

- Ejecutar los tests en CI y guardar los informes HTML y XML como artefactos (GitHub Actions o similar).
- Incluir capturas de la ejecución (ya añadida `documentacion/capturas/TestSalida.png`).
- Añadir un pequeño resumen tabulado en el README del proyecto con los resultados (tiempos y tamaños) para cada ejecución de volumen.

---

## Detalle de tests (por fichero)

A continuación se documenta, fichero por fichero, qué comprueba cada prueba, cuál es su contrato (entradas/salidas), los pasos que realiza, las aserciones que hace y los casos límite que cubre. Esta información facilita entender la cobertura de pruebas y preparar evidencias para RA8.

---

### 1) `AppRepositoryTest.kt`
- Ruta: `app/src/test/kotlin/com/example/tests/AppRepositoryTest.kt`
- Objetivo: verificar la persistencia local por usuario (guardar/leer perfiles, credenciales, tareas y eventos) y las operaciones de limpieza y gestión de tutorizados.

Contrato (inputs / outputs / efectos):
- Inputs: context (ApplicationProvider), objetos `User`, `Task`, `CalendarEvent`, emails.
- Outputs/efectos: archivos JSON privados por usuario con la información correspondiente; valores devueltos por las funciones de lectura (`loadUser`, `loadTasks`, `loadEvents`, `loadTutorizados`).
- Error modes: devuelve listas vacías o null si no existe el fichero; captura excepciones internas para no lanzar en producción.

Pasos que realiza cada test (resumen):
- `saveAndLoadUser_roundtrip`: guarda un `User` y carga el perfil; aserciones sobre `name` y `email`.
- `saveAndLoadTasks_roundtrip`: guarda 2 tareas y verifica que `loadTasks` devuelve ambas con los campos correctos.
- `saveAndLoadEvents_roundtrip`: guarda 2 eventos (uno con hora y otro sin hora) y verifica lectura correcta y preservación de `time=null`.
- `clearAllData_removesFiles`: guarda datos, llama `clearAllData` y verifica que `loadUser` devuelve `null`.
- `tutorizados_management`: crea/añade/elimina tutorizados y verifica `loadTutorizados`.

Aserciones principales:
- El tamaño de las listas devueltas coincide con lo esperado.
- Los campos clave (id, title, status, email) se conservan correctamente.
- `clearAllData` elimina ficheros y produce `null` o listas vacías.

Casos límite cubiertos:
- Lectura cuando no existe fichero (espera lista vacía o null).
- Serialización de campos opcionales (`avatarUri`, `time`) y su correcta recuperación.

Relación con RA8:
- RA8.c (regresión): detecta cambios que rompan la serialización/deserialización.
- RA8.f (uso de recursos, parcialmente): `clearAllData` comprueba liberación de ficheros, útil como evidencia básica.

---

### 2) `CalendarioAlarmsTest.kt`
- Ruta: `app/src/test/kotlin/com/example/tests/CalendarioAlarmsTest.kt`
- Objetivo: probar la lógica de programación y cancelación de alarmas (funciones puras/logic) y creación de canal de notificación.

Contrato:
- Inputs: `Context` (Robolectric), instancias `CalendarEvent` con `date` y `time`.
- Outputs/efectos: llamadas a `AlarmManager` (simuladas por `ShadowAlarmManager`) y creación de `NotificationChannel` en el `NotificationManager` del `Context`.

Pasos y aserciones:
- `ensureNotificationChannel_doesNotCrash`: ejecuta la función y verifica que no lanza excepción (canal creado o ya existente).
- `scheduleAlarm_past_event_is_not_scheduled`: intenta programar un evento en el pasado y comprueba que `ShadowAlarmManager` no tiene alarmas programadas.
- `scheduleAndCancel_alarm_is_scheduled_then_cancelled`: programa un evento futuro, verifica que al menos una alarma queda programada y luego llama a `cancelAlarm` para comprobar que queda cancelada.

Casos límite:
- Evento con fecha en el pasado (no programar alarma).
- Cancelación cuando no existe alarma (comportamiento seguro, no crash).

Relación con RA8:
- RA8.c (regresión): valida comportamiento crítico del scheduling.
- RA8.e (seguridad): indirecta (no cubre permisos exactos aquí), pero útil para comprobar lógica.

---

### 3) `CalendarioAlarmsSecurityTest.kt`
- Ruta: `app/src/test/kotlin/com/example/tests/CalendarioAlarmsSecurityTest.kt`
- Objetivo: probar el manejo de `SecurityException` cuando se intenta programar alarmas exactas y comprobar que el sistema hace fallback a una alarma inexacta.

Contrato:
- Inputs: `Context`, evento futuro `CalendarEvent`, implementación fake de `AlarmScheduler` que simula lanzar `SecurityException` en `setExactAndAllowWhileIdle`.
- Outputs/efectos: llamadas registradas en el fake (`exact`, `set`) que permiten comprobar el orden/flujo.

Pasos y aserciones:
- Preparar `FakeScheduler` cuya `setExactAndAllowWhileIdle` lanza `SecurityException` y cuya `set` registra una llamada.
- Llamar `scheduleAlarm(ctx, event, fakeScheduler)`.
- Aserciones: el fake debe haber registrado una llamada `exact` (intentada) y luego `set` (fallback) — ambas deben ocurrir.

Casos límite:
- `canScheduleExactAlarms()` devolviendo `false` (cubre el camino directo a fallback sin excepción si se desea ampliar).

Relación con RA8:
- RA8.e (pruebas de seguridad): cubre explícitamente la reacción del sistema ante la denegación de permiso `SCHEDULE_EXACT_ALARM`.

---

### 4) `VolumeTests.kt` (prueba de volumen básica)
- Ruta: `app/src/test/kotlin/com/example/tests/VolumeTests.kt`
- Objetivo: comprobar el comportamiento de persistencia con un volumen moderado de datos (500 eventos / 500 tareas) y recoger medidas básicas de tiempo y tamaño en disco.

Contrato:
- Inputs: `Context`, `n` elementos (en nuestro caso 500). Los objetos construidos son `CalendarEvent` y `Task` con campos sencillos.
- Outputs/efectos: ficheros JSON en almacenamiento privado del app, tiempo en ms de operación de guardado y tamaño del fichero en bytes.

Pasos y aserciones:
- `saveManyEvents_and_measure_size`:
  - Genera `n` eventos y llama `AppRepository.saveEvents`.
  - Mide tiempo transcurrido y carga los eventos con `loadEvents`.
  - Aserta que `loaded.size == n` y que el fichero existe y tiene tamaño > 0.
- `saveManyTasks_and_measure_size`:
  - Igual para `Task`/`saveTasks`/`loadTasks`.
  - Imprime en consola las mediciones (útil para inspección manual y captura en CI).

Casos límite y consideraciones:
- El test escribe en el filesystem del contexto de la app (Robolectric simula esto). En CI podrías reducir `n` o usar un backend en memoria para pruebas rápidas.
- Para transformar esta prueba en una prueba de estrés real, aumentar `n` a 1000–2000 y fijar umbrales de tiempo/size en aserciones (por ejemplo: < X ms, < Y bytes).

Relación con RA8:
- RA8.d (pruebas de volumen/estrés): este test proporciona evidencia básica de escalabilidad y rendimiento de la persistencia.
- RA8.f (uso de recursos): mide tamaño en disco y tiempo de escritura.

---

## Cómo interpretar resultados y reportes
- Los tests unitarios se ejecutan con Gradle y Robolectric; el informe legible se genera en `app/build/reports/tests/testDebugUnitTest/index.html`.
- Para cada prueba, el HTML report incluye tiempo de ejecución, mensajes `println` (los que imprimen el tamaño/tiempo) y stacktraces en caso de fallo.
- Para evidencias de RA8 guarda el HTML y los XML de `app/build/test-results/testDebugUnitTest/` como artefactos en CI.

---

Si quieres que añada una tabla automática con los resultados (tiempo y tamaño) extraídos de las ejecuciones recientes y la incluya en este MD, lo hago ahora: copiaré los valores impresos por `VolumeTests` desde la última ejecución y los añadiré a la sección "Resultado de la ejecución (ejemplo)". También puedo copiar el informe HTML dentro de `documentacion/test-reports` para centralizar la evidencia.

