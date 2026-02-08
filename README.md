# TutorOrganiza — Módulo app (introducción y guía rápida)

Resumen ejecutivo
-----------------
TutorOrganiza es una aplicación móvil desarrollada con Jetpack Compose y Material3 cuyo propósito es facilitar la organización diaria de personas que requieren apoyo por parte de un tutor (usuarios asistidos). La aplicación proporciona mecanismos para que un tutor asigne y gestione tareas, eventos y colecciones de material audiovisual, y para que el usuario asistido vea y complete las actividades asignadas.

Objetivo de este README
-----------------------
Documento orientado a revisores y desarrolladores. Resume la estructura del código, la localización de la documentación y las evidencias relacionadas con los criterios de evaluación (RAs). Los enlaces concretos a evidencias (capturas, informes y secciones de la documentación) se insertarán posteriormente: en este README se indican las rutas y los archivos donde localizar dichas evidencias.

Estructura principal del proyecto
--------------------------------
- Código fuente:
  - `app/src/main/java/.../pantallas/`  — Implementación de pantallas (Login, Home, Tareas, Calendario, Vídeos, Tutor, etc.).
  - `app/src/main/java/.../utils/`      — Componentes Compose reutilizables.
  - `app/src/main/java/.../model/`      — Modelos de datos (`User`, `Task`, `CalendarEvent`, ...).
  - `app/src/main/java/.../repository/` — Persistencia (`AppRepository`).
  - `app/src/main/java/.../security/`   — Utilidades de seguridad y biometría.
  - `app/src/main/java/.../receivers/`  — BroadcastReceivers y alarmas.

- Documentación (carpeta `documentacion/`):
  - `ARCHITECTURE.md` — Arquitectura y decisiones de diseño.
  - `COMPONENTS.md`   — Componentes reutilizables y su API.
  - `NUI.md`          — Propuestas de interfaces naturales (biometría, voz, gestos) y justificación.
  - `USER_MANUAL.md`  — Manual de usuario orientado a tutores y usuarios asistidos.
  - `TESTS.md`        — Resumen de pruebas existentes y cómo ejecutarlas.
  - `PERSISTENCE.md`, `SECURITY.md`, `DEV_SETUP.md`, `INFORMES.MD` — Documentación técnica complementaria.
  - `capturas/`       — Imágenes y capturas de pantalla que evidencian funcionalidades.

- Tests:
  - [Tests](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/test/kotlin/com/example/tests) — Tests unitarios y pruebas con Robolectric.
  - Resultados de ejecución y reportes: `app/build/reports/tests/`, `app/build/test-results/`.

Evidencias por criterios (RAs) — localización y estado
-----------------------------------------------------
Este apartado señala en qué documentos o artefactos encontrar evidencia para cada RA. En algunos casos la evaluación se alcanza citando la posibilidad o proponiendo su integración; en otros casos existe implementación o tests automatizados.

- RA1 (Interfaz y desarrollo):
  - RA1.a (análisis herramientas/librerías): ver [`LIBRARIES.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/LIBRARIES.md) y [`ARCHITECTURE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/ARCHITECTURE.md). (Evidencia: análisis de dependencias y justificación de elecciones).
  - RA1.b–d (interfaz, layouts, personalización): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md), [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md).
  - RA1.e–f (análisis y modificaciones de código): ver [`ARCHITECTURE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/ARCHITECTURE.md) y [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (Evidencia: decisiones y cambios documentados).
  - RA1.g (asociación de eventos): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [`pantallas/CalendarioPantalla.kt`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/CalendarioPantalla.kt) (Evidencia: calendario y scheduling de eventos; notas sobre permisos en [`PERSISTENCE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/PERSISTENCE.md) / [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/PERSISTENCE.md)).
  - RA1.h (app integrada): evidencia en [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y en el conjunto de pantallas.

- RA2 (NUI):
  - RA2.a–f: ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md). El documento contiene propuestas (biometría, voz, detección gestual) y justificaciones para el público objetivo; la implantación concreta se identifica como posible mejora y no es necesaria su ejecución completa para la evidencia teórica.

- RA3 (Componentes y reutilización):
  - RA3.a–h: ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [`pantallas/TutorPantalla.kt`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/TutorPantalla.kt) (Evidencia: componentes reutilizables, parámetros y ejemplos de uso).

- RA4 (Usabilidad y estándares):
  - RA4.a–j: ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md), [`ARCHITECTURE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/ARCHITECTURE.md) y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) (Evidencia: decisiones de diseño, menús, distribución de acciones y controles). Documentación de pruebas de usabilidad y evaluación en [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md).

- RA5 (Informes):
  - RA5.a–e: ver [`INFORMES.MD`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/INFORMES.MD) (documento específico que describe cómo se podrían generar informes a partir de los datos, filtros, cálculos y representación gráfica). En el repositorio hay una propuesta y ejemplo de exportación descritos; implementación completa es opcional y puede mencionarse en la documentación como solución viable.

- RA6 (Ayudas y manuales):
  - RA6.a–g: ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md), [`SECURITY.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/SECURITY.md) y [`INFORMES.MD`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/INFORMES.MD) (Evidencia: manual de usuario, propuestas de ayudas contextuales y documentación de persistencia y configuración técnica).

- RA8 (Pruebas):
  - RA8.a–g: ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) y la carpeta de tests [`app/src/test/kotlin/...`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/test/kotlin/com/example/tests). [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) describe la estrategia, pruebas unitarias presentes y áreas por cubrir (notificaciones en background, exact alarms, integración de biometría).

Estado general sobre evidencia
------------------------------
- Documentación: completa y centralizada en [`documentacion/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/documentacion). Tono y orientación adaptados al público docente.
- Implementación: la app contiene pantallas y componentes que sostienen la propuesta; varias funcionalidades (notificaciones exactas, test de background, exportes elaborados) están diseñadas y documentadas pero requieren trabajo adicional para producción.
- Tests: existen tests unitarios ejecutables; algunos fallos y ajustes con Robolectric fueron resueltos durante el ciclo de pruebas (ver `TESTS.md`).

Comandos de uso y compilación (PowerShell)
------------------------------------------
Abrir PowerShell en la raíz del proyecto y ejecutar:

```powershell
# Limpiar y compilar
.\gradlew.bat clean build

# Generar APK debug
.\gradlew.bat :app:assembleDebug

# Ejecutar tests unitarios
.\gradlew.bat testDebugUnitTest
```

Notas finales para el revisor
----------------------------
- Este README tiene un propósito orientador: indica dónde localizar evidencia y artefactos. Los enlaces directos a capturas, informes y secciones internas se insertarán en las ubicaciones marcadas cuando sea necesario.
- La documentación incluye propuestas de mejora señaladas como tal; la mención de una técnica o herramienta es válida para algunos criterios RA cuando se justifica teóricamente en la documentación.

Ubicación de la documentación y contacto
---------------------------------------
- Carpeta de documentación: `documentacion/` (en la raíz del repositorio).
- Para cualquier aclaración sobre la entrega o la evidencia, consulte los documentos referenciados o contacte al responsable del proyecto.
