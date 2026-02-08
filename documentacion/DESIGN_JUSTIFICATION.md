# Justificación de diseño y accesibilidad — TutorOrganiza (resumen ejecutivo)

Este documento ofrece una versión ejecutiva y no repetitiva de las decisiones de diseño, usabilidad y accesibilidad adoptadas en TutorOrganiza. Su propósito es facilitar la lectura al evaluador: presenta los principios y decisiones clave y remite a los documentos técnicos donde se desarrollan los detalles.

Nota: para detalles técnicos y ejemplos de código consulte los documentos referenciados al final de cada sección.

1. Objetivo (resumen)
---------------------
Facilitar una experiencia clara y consistente para dos perfiles: el tutor (gestor de la organización diaria) y el usuario asistido (ejecutor de las tareas). Las decisiones reducen la carga cognitiva del usuario asistido y proporcionan flujos eficientes para el tutor.

Ver: `USER_MANUAL.md`, `ARCHITECTURE.md`

2. Principios de diseño (clave)
-------------------------------
- Simplicidad funcional: una tarea primaria por pantalla.
- Consistencia visual y jerarquía de información.
- Retroalimentación clara para cambios de estado.
- Prevención de errores mediante restricciones de rol y confirmaciones.

Ver: `COMPONENTS.md`, `USER_MANUAL.md`

3. Accesibilidad (puntos esenciales)
------------------------------------
- Contraste y tipografías configurables; tamaños base adaptables.
- Áreas táctiles mínimas (48x48 dp) y espaciado suficiente.
- Soporte para lectores de pantalla (contentDescription y orden lógico de foco).
- Mensajes claros y pictogramas acompañados de texto en acciones clave.

Ver: `COMPONENTS.md`, `NUI.md`

4. NUI y multimodalidad (decisión resumida)
------------------------------------------
- Biometría (inicio rápido) y TTS documentados como opciones de accesibilidad.
- STT y gestos planteados como mejoras viables; su integración queda detallada en `NUI.md`.

Ver: `NUI.md`, `security/BiometricHelper.kt`

5. Componentes y layouts (decisión técnica)
-------------------------------------------
- Elección tecnológica: Jetpack Compose + Material3 (facilita theming y accesibilidad).
- Componentes reutilizables (p. ej. `TaskCard`, `TutorizadoCard`) expuestos con parámetros y callbacks para testabilidad.
- Calendario y alarmas: uso de `AlarmManager` documentado; se señalan limitaciones y fallbacks para `SCHEDULE_EXACT_ALARM`.

Ver: `COMPONENTS.md`, `ARCHITECTURE.md`, `PERSISTENCE.md`

6. Flujo tutor ↔ usuario asistido (resumen UX)
---------------------------------------------
- Tutor: listado de usuarios asistidos en cards expandibles con acciones para añadir/editar tareas, eventos y vídeos.
- Usuario: interfaz simplificada con foco en la ejecución de actividades y confirmaciones.
- Login rápido: vista de cuentas con foto y biometría opcional.

Ver: `USER_MANUAL.md`, `pantallas/TutorPantalla.kt`

7. Multimedia y reproducción (resumen)
-------------------------------------
- Reproducción recomendada: ExoPlayer (local + remoto).
- Para vídeos embebidos (YouTube) se describen alternativas y limitaciones en `COMPONENTS.md`.

Ver: `COMPONENTS.md`

8. Notificaciones y alarmas (resumen operativo)
----------------------------------------------
- NotificationChannel para recordatorios con sonido del sistema.
- Documentada la necesidad de permisos/gestión para alarmas exactas en Android 13+ y el fallback a WorkManager.
- Recomendación: borrar referencia a "última cuenta" al cerrar sesión para evitar relog no deseado.

Ver: `PERSISTENCE.md`, `TESTS.md`

9. Seguridad y privacidad (resumen)
----------------------------------
- Datos personales en ficheros JSON por usuario (migración a Room y cifrado recomendada).
- Biometría delegada al sistema; no se almacenan plantillas biométricas.
- Permisos documentados en `SECURITY.md`.

Ver: `SECURITY.md`, `ARCHITECTURE.md`

10. Testing y validación (resumen)
----------------------------------
- Tests existentes: tests unitarios en `app/src/test/kotlin/...` y algunos tests Robolectric; ver `TESTS.md`.
- Recomendaciones prioritarias: pruebas de notificaciones en background, persistencia de sesión y flujo tutor→añadir tarea (instrumented/Espresso).

Ver: `TESTS.md`
