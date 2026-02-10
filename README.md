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
  - `capturas/`       — Imágenes y capturas de pantalla.

- Tests:
  - [Tests](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/test/kotlin/com/example/tests) — Tests unitarios y pruebas con Robolectric.
  - Resultados de ejecución y reportes: `app/build/reports/tests/`, `app/build/test-results/`.

Evidencias por criterios (RAs) — localización y estado
-----------------------------------------------------
Este apartado señala en qué documentos o artefactos encontrar evidencia para cada RA. En algunos casos la evaluación se alcanza citando la posibilidad o proponiendo su integración; en otros casos existe implementación o tests automatizados.

Nota: además de la documentación y los tests listados a continuación, existe un vídeo de demostración grabado que muestra la aplicación en funcionamiento y explica flujos clave (login, gestión de tareas/eventos, generación de informes). Este vídeo puede añadirse como evidencia visual y es útil para justificar decisiones de diseño y resultados de pruebas.

- RA1 (Interfaz y desarrollo):
  - RA1.a (análisis herramientas/librerías): ver [`LIBRARIES.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/LIBRARIES.md) y [`ARCHITECTURE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/ARCHITECTURE.md). (Evidencia: lista de dependencias, justificación técnica y elección de tecnologías.)
  - RA1.b (crea interfaz gráfica): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y los ficheros de pantallas en [pantallas](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas) (Login, Home, Tutor, Calendario). (Evidencia: definición de pantallas Compose y ejemplo de flujos UI.)
  - RA1.c (uso de layouts y posicionamiento): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [utils](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils) (componentes de layout). (Evidencia: uso de Column/Row/Box, ejemplos de jerarquía visual y cards de resumen.)
  - RA1.d (personalización de componentes): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [utils/TutorComponente.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/TutorComponente.kt), [utils/TareaComponente.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/TareaComponente.kt). (Evidencia: componentes reutilizables y temas Material3 aplicados.)
  - RA1.e (análisis del código): ver [`ARCHITECTURE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/ARCHITECTURE.md) y [`DESIGN_JUSTIFICATION.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DESIGN_JUSTIFICATION.md). (Evidencia: decisiones arquitectónicas, trade-offs y justificación técnica.)
  - RA1.f (modificación del código): ver historial de cambios y [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) para instrucciones de desarrollo; además referencias en `documentacion/` que describen cambios relevantes. (Evidencia: descripción de cambios y puntos de integración.)
  - RA1.g (asociación de eventos): ver [CalendarioPantalla.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/CalendarioPantalla.kt), [CalendarioAlarms.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/CalendarioAlarms.kt) y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md). (Evidencia: creación/edición de eventos, scheduling y manejo de callbacks/acciones.)
  - RA1.h (app integrada): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y conjunto de pantallas ([`pantallas/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas)). (Evidencia: flujos integrados que muestran uso real de la app.)
  
  Nota: el vídeo de demostración muestra un recorrido completo por la aplicación (tutor ↔ usuario), lo que facilita validar la integración de pantallas y flujos descritos en la documentación.

- RA2 (NUI - Interfaces naturales):
  - RA2.a (herramientas NUI): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md) y [BiometricUtils.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/security/BiometricUtils.kt). (Evidencia: identificación de biometría, voz y detección como opciones y referencias a APIs.)
  - RA2.b (diseño conceptual NUI): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md) y [`DESIGN_JUSTIFICATION.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DESIGN_JUSTIFICATION.md). (Evidencia: propuesta de flujos multimodales y justificación para el público objetivo.)
  - RA2.c (interacción por voz): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md) (propuesta STT/TTS). (Evidencia: planteamiento de integración y casos de uso; documentación suficiente para la evaluación teórica.)
  - RA2.d (interacción por gesto): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md). (Evidencia: propuestas y herramientas sugeridas.)
  - RA2.e (detección facial/corporal): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md). (Evidencia: planteamiento, consideraciones éticas y sugerencias de herramientas ML.)
  - RA2.f (realidad aumentada): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md). (Evidencia: propuesta y nivel de integración recomendado.)

- RA3 (Componentes y reutilización):
  - RA3.a (herramientas de componentes): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [utils](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils). (Evidencia: lista de componentes y librerías empleadas.)
  - RA3.b (componentes reutilizables): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [TutorComponente.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/TutorComponente.kt), [TareaComponente.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/TareaComponente.kt). (Evidencia: cards y componentes que se reutilizan en varias pantallas.)
  - RA3.c (parámetros y defaults): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) (descripción de API por componente). (Evidencia: parametrización y valores por defecto documentados.)
  - RA3.d (eventos en componentes): ver los callbacks documentados en [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y su uso en [`pantallas/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas). (Evidencia: onClick, onStatusChange, onExpandChange y demás hooks.)
  - RA3.f (documentación de componentes): ver [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md) y [`documentacion/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/documentacion) en general. (Evidencia: documentación centralizada y ejemplos de uso.)
  - RA3.h (integración en la app): ver [HomePantalla.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/HomePantalla.kt), [TutorPantalla.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/TutorPantalla.kt). (Evidencia: cómo los componentes se usan en múltiples pantallas.)

- RA4 (Usabilidad, estándares y evaluación):
  - RA4.a (estándares): ver [`DESIGN_JUSTIFICATION.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DESIGN_JUSTIFICATION.md) y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md). (Evidencia: criterios de diseño y uso de Material3.)
  - RA4.b (valoración de estándares): ver [`DESIGN_JUSTIFICATION.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DESIGN_JUSTIFICATION.md). (Evidencia: reflexión sobre accesibilidad y decisiones de estilo.)
  - RA4.c (menús): ver [utils/Componentes.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/Componentes.kt) (`AppTopBar` / `SettingsDrawer`) y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md). (Evidencia: estructura de menús y accesos.)
  - RA4.d–e (distribución de acciones y controles): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md). (Evidencia: distribución en la UI, jerarquía de acciones y controles en pantallas principales.)
  - RA4.f (elección de controles): ver `DESIGN_JUSTIFICATION.md` y [`COMPONENTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/COMPONENTS.md). (Evidencia: justificación de tipos de control y su adecuación.)
  - RA4.g (diseño visual): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md). (Evidencia: apariencia y contraste en las pantallas principales.)
  - RA4.h (claridad de mensajes): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) (mensajes y textos) y [`DESIGN_JUSTIFICATION.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DESIGN_JUSTIFICATION.md). (Evidencia: redacción de mensajes y feedback al usuario.)
  - RA4.i–j (pruebas de usabilidad y evaluación en dispositivos): ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) y [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (instrucciones para pruebas), además del vídeo. (Evidencia: plan de pruebas y notas sobre pruebas en dispositivos/emuladores y pruebas en un dispositivo real.)

  Nota: el vídeo de demostración incluye ejemplos de interacción y ciertos escenarios de usabilidad que complementan las capturas y permiten al evaluador observar tiempos, transiciones y respuestas de la UI.

- RA5 (Informes):
  - RA5.a (estructura del informe): ver [`INFORMES.MD`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/INFORMES.MD). (Evidencia: definición del contenido del informe y su estructura.)
  - RA5.b (generación de informes): ver [ReportGenerator.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/ReportGenerator.kt) y [pantallas/Reports](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas). (Evidencia: funciones que construyen texto de informe y resumen numérico.)

  Nota: en el vídeo de demostración se muestra la generación de informes y el uso de filtros, lo que ayuda a verificar visualmente que los datos y gráficos se corresponden con la documentación escrita.

  - RA5.c (filtros sobre valores): ver [`INFORMES.MD`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/INFORMES.MD) y la implementación de [`ReportFilters`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/model/ReportFilters.kt) en [`model/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/model). (Evidencia: modelo de filtros y uso en UI.)
  - RA5.d (valores calculados/recuentos): ver [model/ReportSummary.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/model/ReportSummary.kt) y [ReportGenerator.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/ReportGenerator.kt). (Evidencia: cálculos de totales y recuentos por estado.)
  - RA5.e (gráficos generados): ver [utils/ReportChart.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/utils/ReportChart.kt) y la integración en [HomePantalla.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/HomePantalla.kt)/[TutorPantalla.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/java/com/example/myproyectofinal_din_carloscaramecerero/pantallas/TutorPantalla.kt). (Evidencia: componente gráfico y colores del tema aplicados.)

- RA6 (Ayudas, manuales y documentación persistente):
  - RA6.a (identificación de sistemas de generación de ayudas): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md). (Evidencia: identificación de herramientas y propuestas de ayudas multimodales.)
  - RA6.b (generación de ayudas en formatos habituales): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y [`INFORMES.MD`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/INFORMES.MD) (exportación TXT/CSV propuesta). (Evidencia: procedimientos y exportación básica a TXT.)
  - RA6.c (ayudas sensibles al contexto): ver [`NUI.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/NUI.md) y [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) (propuestas para TTS/STT). (Evidencia: planteamiento de ayudas contextuales.)
  - RA6.d (documenta la estructura de la información persistente): ver [`PERSISTENCE.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/PERSISTENCE.md). (Evidencia: descripción de ficheros JSON y estructura de datos.)
  - RA6.e–f (manual de usuario y manual técnico): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) y [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) / [`SECURITY.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/SECURITY.md). (Evidencia: manuales y guía técnica de instalación/seguridad.)
  - RA6.g (confección de tutoriales): ver [`USER_MANUAL.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/USER_MANUAL.md) (secciones de "primeros pasos" y flujo tutor). (Evidencia: tutoriales básicos y pasos guiados.)

- RA7 (Distribución de aplicaciones - FFOE):
  - RA7.a (empaquetado): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (comandos para `assemble` / `bundle`). (Evidencia: instrucciones y rutas de salida de APK/AAB.)
  - RA7.b (personalización del instalador): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) y  [`app/src/main/AndroidManifest.xml`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/main/AndroidManifest.xml) (iconos, metadata, versionCode). (Evidencia: elementos a modificar para el instalador.)
  - RA7.c (paquete desde el entorno/CI): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (sugerencias CI/GitHub Actions). (Evidencia: flujo CI propuesto para generar bundles.)
  - RA7.d (herramientas externas): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (Fastlane, bundletool, Firebase). (Evidencia: herramientas sugeridas para distribución.)
  - RA7.e (firma digital): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) (firma y keystore) y [`app/build.gradle.kts`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/build.gradle.kts) (signingConfigs). (Evidencia: configuración y pasos para firmar releases.)
  - RA7.f–g–h (instalación desatendida, desinstalación, canales): ver [`DEV_SETUP.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/DEV_SETUP.md) y secciones de distribución propuestas. (Evidencia: recomendaciones y canales de distribución sugeridos.)

- RA8 (Pruebas avanzadas):
  - RA8.a (estrategia de pruebas): ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md). (Evidencia: estrategia general y mapeo a RA8.)
  - RA8.b (pruebas de integración): ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) y los tests en [app/src/test/kotlin/...](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/app/src/test/kotlin/com/example/tests) (Robolectric/unit tests). (Evidencia: tests unitarios y de integración parcial; logs y reportes generados.)

  Nota: el vídeo incluye una demostración de algunos flujos que se usan en los tests (por ejemplo creación de tareas y programación de alarmas), lo que puede ayudar a reproducir manualmente los casos de prueba si el revisor desea verificarlos en dispositivo.

  - RA8.c (pruebas de regresión): ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) y [AppRepositoryTest.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/test/kotlin/com/example/tests/AppRepositoryTest.kt). (Evidencia: tests de roundtrip y regresión sobre persistencia.)
  - RA8.d (pruebas de volumen/estrés): ver [VolumeTests.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/test/kotlin/com/example/tests/VolumeTests.kt) (pruebas de carga moderada en persistencia). (Evidencia: tests que generan múltiples items y miden tiempo/tamaño.)
  - RA8.e (pruebas de seguridad): ver [CalendarioAlarmsSecurityTest.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/test/kotlin/com/example/tests/CalendarioAlarmsSecurityTest.kt) y [`SECURITY.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/SECURITY.md). (Evidencia: tests que simulan excepciones y documentación de riesgos/mitigaciones.)
  - RA8.f (uso de recursos): ver [VolumeTests.kt](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/app/src/test/kotlin/com/example/tests/VolumeTests.kt) y observaciones en [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md). (Evidencia: mediciones básicas de tiempo/tamaño; sugerencias de ampliación.)
  - RA8.g (documentación de pruebas): ver [`TESTS.md`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/blob/master/documentacion/TESTS.md) , los reportes en `app/build/reports/tests/`(Esta carpeta solo se puede ver en el IDE) y una captura de pantalla de BUILD SUCCESFUL en [`documentacion/capturas`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/documentacion/capturas).

Estado general sobre evidencia
------------------------------
- Documentación: completa y centralizada en [`documentacion/`](https://github.com/Carame005/MyProyectoFinal_DIN_CarlosCarameCerero/tree/master/documentacion).
- Implementación: la app contiene pantallas y componentes que sostienen la propuesta; varias funcionalidades (notificaciones exactas, test de background, exportes elaborados) están diseñadas y documentadas pero requieren trabajo adicional para producción.

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
