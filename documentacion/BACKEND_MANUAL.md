# Manual técnico — Backend de la aplicación (AutiCare)

Este documento describe de forma clara y concisa cómo está organizado y funciona el "backend" local de la aplicación (capas de persistencia, repositorios, alarmas/recordatorios, gestión de medios, sesiones y seguridad). Está pensado para un evaluador técnico que revise la arquitectura y las decisiones de implementación sin necesidad de ejecutar el código.

Índice
- Visión general
- Estructura de paquetes y ficheros relevantes
- Modelo de datos (resumen)
- Persistencia (archivos, SharedPreferences, Room o serialización)
- Repositorio (contrato y responsabilidades)
- Sincronización y sesión
- Alarmas y notificaciones (CalendarioAlarms)
- Gestión de medios (videos e imágenes)
- Seguridad y control de accesos
- Exportación de informes (TXT)


Visión general
---------------
La aplicación usa un backend local (sin servidor remoto) compuesto por:
- Una capa de persistencia que guarda usuarios, tareas, eventos y colecciones de vídeo en ficheros o almacenamiento local.
- Un conjunto de repositorios que exponen operaciones de alto nivel para acceder y modificar los datos desde la UI.
- Un subsistema de alarmas y notificaciones que programa recordatorios en el sistema (AlarmManager).
- Mecanismos de sesión y autenticación local (credenciales guardadas, gestión de la sesión activa, opción de biometría).
- Manejo de recursos multimedia (ficheros locales y URLs) con validaciones para evitar referencias inconsistentes.

Estructura de paquetes y ficheros relevantes
-------------------------------------------
A continuación se enumeran las rutas de los ficheros Kotlin clave (ejemplos, según estructura del proyecto):
- app/src/main/java/.../data/
  - AppRepository.kt (o similar): implementación del repositorio principal.
  - LocalDataSource.kt: operaciones de lectura/escritura en disco.
- app/src/main/java/.../model/
  - User.kt, Task.kt, Event.kt, VideoItem.kt: modelos de datos.
- app/src/main/java/.../pantallas/
  - CalendarioAlarms.kt: funciones para programar/cancelar alarmas.
  - TutorPantalla.kt, HomePantalla.kt: llamadas a repositorio y handlers de eventos.
- app/src/main/java/.../util/
  - FileUtils.kt, ReportGenerator.kt: utilidades para exportar TXT y manipular ficheros.

(Nota: sustituir "..." por el paquete real del proyecto cuando se consulte el código.)

Modelo de datos (resumen)
-------------------------
Los objetos principales son:
- User: contiene id, nombre, email, rol (admin/tutor/tutorizado), rutaFoto (opcional), opciones como biometría activada.
- Task: id, ownerId (user), título, descripción, fechaCreacion, fechaVencimiento (opcional), estado (PENDIENTE/EN_PROCESO/HECHO), asignadoPorTutor flag y metadatos.
- Event: id, ownerId, título, descripcion, fecha (día), hora (opcional), alarmaProgramada boolean, asignadoPorTutor flag.
- VideoItem / Collection: id, ownerId, título, tipo (URL / LOCAL), rutaLocal o url, fechaAñadido, asignadoPorTutor flag.

Persistencia
------------
La app persiste datos localmente. Dependiendo de la implementación del proyecto, se emplea una de las siguientes aproximaciones:
- Serialización a ficheros JSON (por modelo) dentro de almacenamiento interno o externo privado.
- Room/SQLite (si está presente en el proyecto) con DAOs para cada entidad.
- SharedPreferences / DataStore para configuraciones y sesión activa.

Buenas prácticas empleadas (o recomendadas):
- Guardar siempre ownerId en las entidades para filtrar por usuario.
- Persistir la sesión activa en un preferencia segura; invalidarla al cerrar sesión.
- Mantener un esquema de backups/copia si se usa almacenamiento en ficheros.

Repositorio (contrato y responsabilidades)
------------------------------------------
El repositorio principal (p. ej. `AppRepository`) actúa como puerta de entrada para la UI:
- Lectura: obtener lista de usuarios, tareas por usuario, eventos por fecha, colecciones de vídeos.
- Escritura: crear/editar/eliminar tareas, eventos, vídeos; marcar tareas como completadas.
- Operaciones específicas: marcar elemento como asignadoPorTutor, exportar datos de un usuario, purgar caché.
- Notificar cambios: el repositorio puede exponer flows / LiveData para que la UI observe los cambios.

Sincronización y sesión
-----------------------
- Sesión persistente: la app guarda la cuenta activa en preferencia (ID del user). Al iniciar la app, si existe sesión válida se carga el usuario.
- Bug conocido: la app restauraba la última cuenta incluso tras cerrar sesión (comportamiento no deseado). Esto debe corregirse invalidando la preferencia al cerrar sesión.
- Biometría: la app sólo guarda una marca de que el usuario puede iniciar con biometría; la verificación real la realiza el framework Android (BiometricPrompt).

Alarmas y notificaciones
------------------------
- `CalendarioAlarms` gestiona la creación y cancelación de alarmas usando `AlarmManager.setExactAndAllowWhileIdle`.
- En Android 12+ y versiones recientes es necesario el permiso `SCHEDULE_EXACT_ALARM` o `USE_EXACT_ALARM` para alarmas exactas; si no está declarado el intento lanzará `SecurityException`.
- Para que las notificaciones aparezcan con sonido incluso con la app cerrada se debe:
  1. Crear y registrar un `NotificationChannel` (ANDROID O+).
  2. Programar `PendingIntent` que dispare un `BroadcastReceiver` que construya y lance la `Notification`.
  3. Asegurarse de que la app tiene permiso de notificaciones y que el `BroadcastReceiver` está declarado o registrado.

Gestión de medios (videos e imágenes)
-------------------------------------
- Al guardar imágenes de perfil, usar rutas accesibles y persistentes (guardar copia en almacenamiento interno privado o persistir el URI correctamente).
- Bug conocido: las imágenes no cargan tras reiniciar la app; causas frecuentes: usar URIs temporales, o no persistir permisos de URI (takePersistableUriPermission).
- Reproducir vídeos: para URLs de YouTube se puede usar WebView con embed o integrar YouTube Player API; para ficheros locales usar ExoPlayer o MediaPlayer con un `SurfaceView`/Compose equivalent.
- Validaciones: al crear un VideoItem se debe exigir exactamente uno de (rutaLocal xor url) para evitar inconsistencias.

Seguridad y control de accesos
------------------------------
- Roles: `isAdmin` / `isTutor` / `isTutorizado` determinan las acciones habilitadas en la UI (e.g., eliminar contenido).
- Validar en la capa de repositorio cualquier operación sensible (eliminar contenido asignado por tutor debe chequear rol).
- No almacenar contraseñas en texto plano; usar hashing (PBKDF2/BCrypt) o delegar a Android AccountManager / credenciales seguras.
- Almacenamiento de tokens o sesiones: usar `EncryptedSharedPreferences` o `Jetpack Security` para datos sensibles.

Exportación de informes (TXT)
-----------------------------
- `ReportGenerator` toma filtros (periodo, tipos: tareas, eventos, vídeos) y consulta al repositorio para construir un resumen.
- Formato: texto plano organizado (encabezado con usuario y periodo, secciones con conteos y listados, pie con fecha de generación).
- Guardado: fichero temporal en caché; opción de compartir mediante `Intent.ACTION_SEND`.

Apéndice: ficheros a revisar (sugeridos)
---------------------------------------
- app/src/main/java/.../data/AppRepository.kt
- app/src/main/java/.../data/LocalDataSource.kt
- app/src/main/java/.../pantallas/CalendarioAlarms.kt
- app/src/main/java/.../pantallas/TutorPantalla.kt
- app/src/main/java/.../util/ReportGenerator.kt
- app/src/main/java/.../model/*.kt


--


