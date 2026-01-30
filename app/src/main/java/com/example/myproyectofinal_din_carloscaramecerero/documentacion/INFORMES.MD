# INFORMES (RA5) — Guía práctica para la app AutiCare

Este documento está dedicado exclusivamente al RA5 (Informes) y explica de forma práctica cómo implementar la generación, visualización y exportación de informes en la aplicación. El resto de RAs está documentado en archivos independientes en la carpeta `documentacion/`.

Objetivo
--------
Proveer una guía clara (MVP → mejoras) para añadir una funcionalidad de informes que permita a tutores y cuidadores generar resúmenes y exportaciones con los datos de un tutorizado (tareas, eventos y vídeos), y dejar evidencias para la evaluación RA5.

Resumen rápido (qué debe hacer la funcionalidad)
- Generar un informe por tutorizado con: nombre, correo, conteo de tareas por estado (pendiente/en proceso/hecho), lista de eventos (fecha/hora/título) y vídeos/colecciones.
- Permitir filtrar por rango de fechas y tipo de dato.
- Exportar el informe en formatos TXT (resumen legible) y CSV (fila por registro). Opcional: PDF.
- Guardar el fichero en almacenamiento interno y mostrar la ruta al tutor (o permitir compartirlo).

MVP recomendado (implementación mínima para RA5)
- `ReportService` (clase en `reports/ReportService.kt`): consulta `AppRepository` y calcula el `ReportSummary` y las filas de detalle.
  - Funciones mínimas:
    - `fun summary(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): ReportSummary`
    - `fun rows(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): List<ReportRow>`
    - `fun exportCsv(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): String` → devuelve ruta del fichero.
- `ReportsScreen` (Compose): UI con filtros (usuario, fecha desde/hasta, tipo), tarjetas resumen y botón `Exportar CSV`.
- Integración puntual en `TutorPantalla`: botón "Exportar informe" por tutorizado (ya existe helper `generateTutorReport` para TXT; enlazar o reutilizar `ReportService.exportCsv`).

Datos y fuentes
---------------
- Origen: `AppRepository` (métodos existentes): `loadTasks`, `loadEvents`, `loadCollections`.
- Campos clave a utilizar:
  - Tarea: id, title, status (PENDING/IN_PROGRESS/DONE), createdByTutor
  - Evento: id, date, time, title, createdByTutor
  - Vídeo: colección, título, uriString, createdByTutor

Cálculos mínimos (obligatorios para RA5)
- Totales: número de tareas totales, completadas, en proceso, pendientes.
- Recuento de eventos en el rango seleccionado.
- Recuento de colecciones y vídeos.
- % cumplimiento tareas = (tareas_completadas / tareas_totales) * 100 (si tareas_totales>0).

Filtros recomendados
- Usuario/tutorizado (o "Todos").
- Rango de fechas (desde / hasta) aplicado a fecha de evento y, opcionalmente, a fecha de creación de tareas.
- Tipo: tareas / eventos / vídeos / todos.

Formatos de exportación
-----------------------
- TXT (resumen legible): útil para impresión o lectura rápida. Puede generarse con `generateTutorReport` (ya implementado).
- CSV (detalle): una fila por registro con columnas: tipo,id,título,fecha,hora,estado,creadoPorTutor.
- PDF (opcional): renderizar HTML y usar Print API o `PdfDocument`.

Visualizaciones (opcional, mejora)
- Gráfica de barras/serie temporal: tareas completadas por día/semana.
- Pie chart: proporción completadas vs pendientes.
- Librerías: `Vico` (Compose charts), `MPAndroidChart` si prefieres View-based charts.

Privacidad y manejo de ficheros
-------------------------------
- Guardar los informes en almacenamiento interno de la app (ctx.openFileOutput) por defecto.
- Documentar en `REPORTS.md` dónde se guardan y cómo eliminarlos (ej.: `AppRepository.clearUserData` o función específica para borrar informes).
- Si se implementa compartir (ACTION_SEND), recordar pedir permiso temporal si el fichero debe estar accesible.

Pruebas y evidencias (qué añadir)
---------------------------------
- `ReportServiceTest` (unitario): usar un `FakeRepository` con datos controlados y validar `summary` y filas. Casos:
  - Sin datos (listas vacías).
  - Datos mixtos con tareas en distintos estados.
  - Rango de fechas que incluya/excluya eventos.
- UI tests (Robolectric + Compose): verificar que `ReportsScreen` muestra valores correctos cuando el `ReportService` devuelve datos conocidos.
- Instrumented test (opcional): en emulador, generar export CSV y comprobar que el fichero existe y contiene las filas esperadas.
- Evidencias a incluir en la documentación:
  - `documentacion/test-reports/sample-report.csv` (ejemplo generado),
  - `documentacion/capturas/report_summary.png` (pantalla del informe),
  - Resultados de tests (HTML/XML) copiados en `documentacion/test-reports/`.

Comandos útiles para desarrollo y pruebas
----------------------------------------
```powershell
# Ejecutar tests unitarios (incluyendo ReportServiceTest)
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.ReportServiceTest" --info

# Ejecutar todos los tests del módulo app
.\gradlew.bat :app:testDebugUnitTest --info

# Construir debug y probar en emulador
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Evidencias que deberías dejar para RA5
-------------------------------------
- `REPORTS.md` o `INFORMES.MD` (este archivo) con la estructura y fórmulas.
- `documentacion/test-reports/sample-report.csv` y/o `sample-report.txt`.
- Capturas de la UI `documentacion/capturas/report_summary.png`.
- Salida de tests unitarios y UI tests (`app/build/reports/tests/...`) copiada a `documentacion/test-reports/`.

Plan de implementación (prioridades)
------------------------------------
1. Implementar `ReportService` y `ReportServiceTest` (alta prioridad, 1 día aprox.).
2. Crear `ReportsScreen` mínimo con filtros y botón `Exportar CSV` (media prioridad, 1 día).
3. Añadir integración del botón de export en `TutorPantalla` (ya hay `generateTutorReport` para TXT; reutilizar o unificar).  
4. Añadir CSV de ejemplo y capturas en `documentacion/` y actualizar `TESTS.md`/`REPORTS.md` con instrucciones.  
5. Opcional: PDF, gráficos y tests instrumentados.

Notas finales
-------------
Este archivo está pensado para ser la referencia exclusiva de RA5 en `documentacion/`. Si quieres, genero también el archivo `REPORTS.md` con ejemplos concretos (plantilla CSV y formato de TXT) o empiezo la implementación del `ReportService` y la pantalla `ReportsScreen` (elige: implementación o documentación adicional).
