# INFORMES (RA5) — Guía práctica para la app (actualizada)

Este documento describe la funcionalidad de generación, visualización y exportación de informes en la aplicación (RA5). Incluye además el estado real de la implementación en el repositorio, instrucciones de uso, limitaciones conocidas y propuestas de pruebas y evidencia para la evaluación.

## Objetivo

Proveer la especificación mínima y ampliada de la funcionalidad de informes para tutores/cuidadores: generar resúmenes y exportaciones con los datos de un tutorizado (tareas, eventos y vídeos).

## Resumen funcional (qué hace la funcionalidad)

- Genera un informe por tutorizado con: correo, conteo de tareas por estado (pendiente/en proceso/hecho), lista de eventos (fecha/hora/título) y recuento de vídeos/colecciones.
- Permite filtrar por periodo (Última semana / Último mes) y por tipo de datos (tareas en estado X, eventos, vídeos).
- Exporta el informe en TXT (resumen legible). Se propone CSV como extensión (ver secciones siguientes).
- Guarda el fichero en almacenamiento interno y permite compartirlo mediante `Intent.ACTION_SEND`.

## Estado actual en el repositorio (implementado)

Estas son las piezas que ya están desarrolladas y su ubicación (resumen práctico para el profesor):

- Generador de texto de informe:
  - `app/src/main/java/.../utils/ReportGenerator.kt`
    - Funciones implementadas relevantes:
      - `buildReportText(context, targetEmail, filters): String` — genera el texto del informe (TXT).
      - `buildReportSummary(context, targetEmail, filters): ReportSummary` — devuelve datos numéricos resumidos para gráficos.
      - `saveReportToCache(context, filename, content): String?` y `getUriForFile(...)` — utilidades para exportar/compartir.

- Modelo resumen para gráficos:
  - `app/src/main/java/.../model/ReportSummary.kt` — `data class ReportSummary(tasksCompleted, tasksInProgress, tasksPending, eventsCount, totalVideos)`

- Componente gráfico (básico, Compose):
  - `app/src/main/java/.../utils/ReportChart.kt` — componente que dibuja un gráfico de barras horizontales (Completadas, En Progreso, Pendientes, Eventos, Vídeos). Se ha integrado en la pantalla `Home`.

- Integración UI (Home y Tutor):
  - `app/src/main/java/.../pantallas/HomePantalla.kt` — botón "Generar informe" que abre un diálogo de filtros y muestra el informe en pantalla junto con el gráfico generado.
  - `app/src/main/java/.../pantallas/TutorPantalla.kt` — ya contiene un flujo para ver/generar informes por tutorizado (diálogo reutiliza `ReportGenerator`).

- Visual y ergonomía:
  - El bloque del informe y el área del gráfico usan un color de fondo uniforme (se estableció como `#35343A` por petición), separador y etiqueta "Gráfico" para mejorar legibilidad.
  - El informe ahora se muestra completo en la pantalla (sin scroll interno); el scroll se asume en la pantalla principal para evitar anidamientos.

> Nota: los ficheros de ejemplo y capturas propuestas (CSV, screenshots) se indican en la sección de pruebas/evidencias. Si deseas que genere muestras automáticas, puedo añadir generación de CSV/TXT de ejemplo y almacenarlas en `documentacion/test-reports/`.

## Uso en la app (cómo generar un informe)

1. Abrir la app e iniciar sesión con un usuario.
2. En la pantalla Home pulsar "Generar informe".
3. Seleccionar el periodo (Última semana / Último mes) y los tipos a incluir (Completadas / En proceso / Pendientes / Eventos / Vídeos).
4. Pulsar "Generar" — el texto del informe aparece en el bloque (completo) y debajo se muestra el gráfico resumen.
5. Opciones del informe: "Copiar" (al portapapeles) y "Compartir" (abre selector con archivo TXT generado en caché y `ACTION_SEND`).


## Capturas relacionadas
Para facilitar la evaluación visual, se incluyen capturas que muestran la ubicación del botón y la integración del UI de informes:

- `documentacion/capturas/menuHome.jpeg` — muestra la pantalla Home y la ubicación del botón "Generar informe".
- `documentacion/capturas/menuTutor.jpeg` — muestra la vista del Tutor con la lista de tutorizados y acceso a generación de informes por tutorizado.
- `documentacion/capturas/TestSalida.png` — captura de la ejecución de tests (build/tests) útil como evidencia complementaria.

## Exportación / formatos

- Actual: exportación a TXT mediante `ReportGenerator.saveReportToCache` y uso de `getUriForFile` para compartir.
- Propuesta (próximo paso): añadir `exportCsv(...)` en una clase `ReportService` para generar un CSV con filas detalladas y exportarlo a `documentacion/test-reports/` para evidencia.

## Detalles técnicos importantes

- Filtros:
  - `ReportFilters` (modelo) controla `period` (LAST_WEEK, LAST_MONTH) y booleanos para incluir/computar tareas por estado, eventos y vídeos.
- Fechas:
  - El generador actual usa `java.time.LocalDate` (API 26+). En el código hay anotaciones y supresiones para compatibilidad, pero si se desea soporte total para minSdk < 26 es recomendable activar core library desugaring en Gradle (ver sección Limitaciones).
- Recuento de tareas:
  - Se cuentan por `TaskStatus` y en el texto del informe se muestra la descripción legible (`TaskStatus.desc`).
- Gráfico:
  - `ReportChart` calcula el máximo para escalar barras y muestra 5 ítems (Completadas, En Progreso, Pendientes, Eventos, Vídeos). Los colores usan el `colorScheme` del tema y el fondo del bloque se fijó a `#35343A` para coherencia visual.

## Limitaciones y recomendaciones

- API / java.time: el uso de `LocalDate` puede provocar advertencias en análisis estático si el minSdk < 26. Recomendaciones:
  - Activar core library desugaring en Gradle para usar `java.time` en minSdk < 26.
  - O alternativamente reescribir la lógica de filtrado por fecha a `Long` timestamps si no se desea tocar Gradle.

- Contraste y accesibilidad: el fondo `#35343A` es oscuro; si el texto con `onSurface` no resulta legible, recomendamos forzar `Color.White` para el contenido textual del informe o ajustar la paleta del tema.

## Pruebas y evidencias requeridas para RA5

- Unit tests recomendados:
  - `ReportGeneratorTest` / `ReportServiceTest` (si se implementa `ReportService`): validar `buildReportText` y `buildReportSummary` con repositorio falso (FakeRepository) con datos controlados.
  - Verificar que el TXT contiene las líneas esperadas y que los contadores coinciden.

- UI tests sugeridos (Robolectric + Compose):
  - `ReportsScreen` muestra los valores correctos para datos de prueba; la acción "Compartir" crea un fichero en caché.

- Evidencias para entregar:
  - `documentacion/test-reports/sample-report.txt` (ejemplo generado) y opcional `sample-report.csv`.
  - Captura: `documentacion/capturas/report_summary.png` (pantalla Home con informe + gráfico visible).
  - Resultado de tests (HTML/XML) en `documentacion/test-reports/`.

## Plan de implementación y prioridades (siguiente pasos)

1. Implementar/extraer `ReportService` como capa que orquesta `AppRepository` y `ReportGenerator` (responsable de summary/rows/exportCsv).
2. Crear `ReportsScreen` Compose (filtros avanzados y export CSV/PDF).
3. Integrar botón "Exportar informe" en `TutorPantalla` para generar CSV por tutorizado y almacenarlo en `documentacion/test-reports/` para evidencia.
4. Añadir tests unitarios para `ReportGenerator`/`ReportService` y tests UI.
5. Habilitar desugaring si se quiere soporte `java.time` en minSdk < 26.

## Comandos útiles

```powershell
# Ejecutar tests unitarios (ejemplo para ReportGenerator/ReportService cuando existan)
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.tests.ReportGeneratorTest" --info

# Compilar el apk debug
.\gradlew.bat :app:assembleDebug
```

---

Si quieres, aplico ahora alguna de las mejoras siguientes y actualizo el documento:

- Añadir implementación mínima de `ReportService` y un test de ejemplo (unitario);
- Generar un `sample-report.txt` y añadirlo a `documentacion/test-reports/`;
- Cambiar el color del texto dentro del bloque de informe a blanco para garantizar contraste sobre `#35343A`.

Indica qué prefieres y lo implemento inmediatamente (edito código y/o documento y ejecuto tests si procede).

- RA5.b (generación de informes): ver [ReportGenerator.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/ReportGenerator.kt) y [pantallas/Reports](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas). (Evidencia: funciones que construyen texto de informe y resumen numérico.)
