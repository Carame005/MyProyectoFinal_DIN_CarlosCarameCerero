# INFORMES (RA5) — Guía práctica para la app AutiCare

Este documento está dedicado exclusivamente al RA5 (Informes) y describe cómo está planteada la funcionalidad de generación, visualización y exportación de informes en la aplicación, así como propuestas de implementación y evidencia requerida para la evaluación.

Objetivo
--------
Proveer la especificación mínima y ampliada de la funcionalidad de informes para tutores/cuidadores: generar resúmenes y exportaciones con los datos de un tutorizado (tareas, eventos y vídeos).

Resumen funcional (qué hace la funcionalidad)
--------------------------------------------
- Genera un informe por tutorizado con: nombre, correo, conteo de tareas por estado (pendiente/en proceso/hecho), lista de eventos (fecha/hora/título) y vídeos/colecciones.
- Permite filtrar por rango de fechas y tipo de dato.
- Exporta el informe en formatos TXT (resumen legible) y CSV (detalle). Opcional: PDF.
- Guarda el fichero en almacenamiento interno y muestra la ruta al tutor (o permite compartirlo mediante `ACTION_SEND`).

Propuestas de implementación (MVP y extensiones)
------------------------------------------------
MVP:
- `ReportService` (clase en `reports/ReportService.kt`): consulta `AppRepository` y calcula el `ReportSummary` y las filas de detalle.
  - Funciones mínimas:
    - `fun summary(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): ReportSummary`
    - `fun rows(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): List<ReportRow>`
    - `fun exportCsv(ctx: Context, userEmail: String, from: LocalDate?, to: LocalDate?): String` → devuelve ruta del fichero.
- `ReportsScreen` (Compose): UI con filtros (usuario, fecha desde/hasta, tipo), tarjetas resumen y botón `Exportar CSV`.
- Integración en `TutorPantalla`: botón "Exportar informe" por tutorizado; existe una función `generateTutorReport` para TXT que puede reutilizarse o unificarse con `ReportService`.

Extensiones recomendadas:
- Generación PDF a partir de HTML y uso de Print API o `PdfDocument`.
- Visualización de gráficos (barras, pasteles) usando librerías de charts (por ejemplo `Vico` o `MPAndroidChart`).

Fuentes de datos y cálculos
--------------------------
- Origen: `AppRepository` (métodos existentes): `loadTasks`, `loadEvents`, `loadCollections`.
- Cálculos mínimos obligatorios:
  - Totales: número de tareas totales, completadas, en proceso, pendientes.
  - Recuento de eventos en el rango seleccionado.
  - Recuento de colecciones y vídeos.
  - % cumplimiento tareas = (tareas_completadas / tareas_totales) * 100 (si tareas_totales>0).

Filtros y formatos
------------------
- Filtros: usuario/tutorizado, rango de fechas (desde / hasta), tipo (tareas / eventos / vídeos / todos).
- Formatos: TXT (resumen), CSV (detalle), PDF (opcional).

Privacidad y almacenamiento
---------------------------
- Almacenamiento por defecto: almacenamiento interno de la app (ctx.openFileOutput).
- Compartición: mediante `Intent.ACTION_SEND` si se requiere compartir fuera de la app.
- Recomendación de manejo: documentar ubicación de informes y política de retención en `INFORMES.MD`.

Pruebas y evidencias requeridas para RA5
---------------------------------------
- `ReportServiceTest` (unitario): tests con `FakeRepository` y datos controlados para validar `summary` y `rows`.
- UI tests (Robolectric + Compose): verificar que `ReportsScreen` muestra valores correctos para datos conocidos.
- Instrumented test (opcional): generar CSV en emulador y comprobar existencia/consistencia del fichero.
- Evidencias a incluir en la documentación:
  - `documentacion/test-reports/sample-report.csv` (ejemplo generado).
  - `documentacion/capturas/report_summary.png` (pantalla del informe).
  - Resultados de tests (HTML/XML) copiados a `documentacion/test-reports/`.

Comandos útiles (desarrollo)
---------------------------
```powershell
# Ejecutar tests unitarios relacionados con informes
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.ReportServiceTest" --info

# Ejecutar todos los tests del módulo app
.\gradlew.bat :app:testDebugUnitTest --info
```

Plan de implementación — prioridades
-----------------------------------
1. Implementación de `ReportService` y `ReportServiceTest`.
2. Creación de `ReportsScreen` mínimo con filtros y export CSV.
3. Integración del botón de export en `TutorPantalla`.
4. Añadir CSV de ejemplo y capturas en `documentacion/` y actualizar `TESTS.md` con instrucciones de ejecución.
