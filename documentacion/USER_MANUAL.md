# Manual de Usuario — AutiCare (versión básica)

Manual dirigido a personas que necesitan que un tutor les lleve una organización diaria y a sus tutores; describe de forma clara y paso a paso el uso de la aplicación en su versión documentada.

Tabla de contenido
- Introducción rápida
- Conceptos básicos (pantallas principales)
- Primeros pasos (configurar cuenta)
- Cómo iniciar sesión
- Inicio rápido (selección por foto + biometría)
- Pantalla principal
- Tareas (añadir, marcar)
- Calendario y eventos (añadir recordatorio)
- Colecciones de vídeos (añadir y reproducir)
- Informes (generar, filtrar y exportar)
- Perfil y ajustes (cambiar nombre, foto y cerrar sesión)
- Notificaciones y recordatorios
- Buenas prácticas para tutores
- Preguntas frecuentes (solución de problemas)

Introducción rápida
-------------------
- Funcionalidad principal: organizar tareas, eventos y colecciones de vídeo; programación de recordatorios; visualización de contenido de forma accesible.
- Público objetivo: personas que requieren apoyo en la organización diaria y sus tutores.

Conceptos básicos (pantallas principales)
-----------------------------------------
- Inicio (Resumen): saludo, fecha y tarjetas con conteo de tareas, eventos y colecciones.
- Tareas: lista de tareas personales y estado de cada una.
- Calendario: vista mensual con eventos por día; posibilidad de añadir hora para recordatorio.
- Progreso / Vídeos: colecciones de vídeos agrupadas por tema.
- Ajustes: panel lateral con opciones de perfil, tema, seguridad y cierre de sesión.

Primeros pasos (configurar cuenta)
----------------------------------
1. Abrir la app: en la pantalla de inicio aparece formulario de inicio de sesión o registro.
2. Registrar una cuenta: seleccionar "Registrarse" y completar nombre, correo y contraseña.
3. Iniciar sesión: introducir correo/usuario y contraseña y seleccionar "Entrar".

Cómo iniciar sesión
-------------------
1. En la pantalla de inicio, introducir el usuario o correo en el campo correspondiente.
2. Escribir la contraseña en el campo de contraseña.
3. Pulsar "Entrar".

Inicio rápido (selección por foto + biometría)
-----------------------------------------------
- Vista rápida con cuentas registradas (foto, nombre y correo). Al pulsar sobre un usuario, si el dispositivo tiene biometría registrada se solicitará verificación biométrica y, si la verificación es correcta, se iniciará sesión.
- Si el dispositivo no dispone de biometría configurada, la selección inicia sesión sin verificación adicional.
- Registro de biometría: se gestiona desde `Ajustes > Seguridad > Registrar biometría`.

Pantalla principal
------------------
- Elementos visibles tras iniciar sesión:
  - Nombre del usuario en la parte superior.
  - Fecha del día.
  - Tarjetas de resumen: Tareas, Eventos, Colecciones (cada tarjeta muestra el número correspondiente).
  - Botón "Generar informe": localizado bajo la tarjeta de Vídeos/Resumen (en Home). Permite crear un informe con resumen de actividades y gráficos.
- Interacción: pulsar una tarjeta para acceder a su contenido detallado.

Tareas (añadir, marcar)
-----------------------
- Ver tareas: acceder a la pestaña Tareas o pulsar la tarjeta "Tareas".
- Añadir tarea: pulsar el botón flotante "+" → completar título y descripción → pulsar "Añadir".
- Cambiar estado: desplegar la tarea y seleccionar "En proceso" o "Hecho".
- Eliminación: los usuarios con `isAdmin=false` no disponen del icono de eliminación para tareas/vídeos/eventos asignados por un tutor.

Calendario y eventos (añadir recordatorio)
------------------------------------------
- Navegar por meses con flechas.
- Seleccionar día y visualizar eventos del día.
- Añadir evento: pulsar "+" → escribir título → seleccionar día (obligatorio) → (opcional) seleccionar hora para recordatorio → guardar.
- Eliminación: restricción para usuarios sin rol de administrador cuando el evento fue asignado por un tutor.

Colecciones de vídeos (añadir y reproducir)
-------------------------------------------
- Acceder a colecciones desde la pestaña "Progreso / Vídeos".
- Crear colección: pulsar "+" → escribir título → crear.
- Añadir vídeo: en la colección pulsar "añadir vídeo" → elegir archivo local o introducir URL (no se admiten ambos simultáneamente) → completar metadatos → añadir.
- Reproducción: pulsar el icono de reproducir; el reproductor permite expandir a pantalla completa.
- Eliminación: restricción para usuarios sin rol administrador en contenidos asignados por tutor.

Informes (generar, filtrar y exportar)
--------------------------------------
La aplicación incluye una funcionalidad para generar informes resumidos que ayudan al tutor y al usuario a visualizar el estado de las actividades y eventos.

Dónde encontrar el botón
- En la pantalla `Home` hay un botón "Generar informe" ubicado bajo la tarjeta de Vídeos/Resumen.
- En la pantalla `Tutor` también hay opción para generar informes por cada tutorizado (desde la card expandida del usuario o el panel de la lista de tutorizados).

Cómo generar un informe (paso a paso)
1. Pulsar "Generar informe" en la pantalla `Home` o en la `TutorPantalla` sobre el tutorizado deseado.
2. Seleccionar el periodo: "Última semana" o "Último mes".
3. Marcar los tipos de datos a incluir: Tareas (Completadas / En proceso / Pendientes), Eventos, Vídeos.
4. Pulsar "Generar".

Qué muestra el informe
- Resumen numérico: conteo de tareas por estado (Completadas, En proceso, Pendientes), número de eventos en el periodo y número total de vídeos/colecciones añadidos.
- Gráfico: barras que representan visualmente los recuentos seleccionados.
- Texto detallado: listado legible con las entradas relevantes (títulos y fechas de eventos; recuentos de tareas).

Exportación y compartir
- Después de generar el informe puede copiarse al portapapeles o compartirse mediante el botón "Compartir".
- El fichero se genera como TXT y se guarda temporalmente en caché; el sistema abrirá el selector para que puedas elegir la app con la que compartir o exportar (por ejemplo, correo, Drive, etc.).
- Los tutores pueden generar informes de sus tutorizados y descargar/compartir el fichero resultante.

Comportamiento cuando no hay datos
- Si no existen elementos para el filtro seleccionado, en vez de dejar espacios en blanco el informe mostrará mensajes claros: "No hay tareas", "No hay eventos" o "No hay vídeos" según corresponda.

Perfil y ajustes (cambiar nombre, foto y cerrar sesión)
-------------------------------------------------------
- Abrir perfil: pulsar el avatar en la barra superior.
- Cambiar foto: pulsar la foto y seleccionar una nueva desde la galería.
- Ajustes: abrir el panel lateral para cambiar nombre, contraseña, tema o registrar biometría.
- Cerrar sesión: opción disponible en ajustes.

Notificaciones y recordatorios
------------------------------
- Para recibir recordatorios el dispositivo debe permitir notificaciones para la app.
- Verificación en caso de ausencia de notificaciones: comprobar ajustes del dispositivo y la hora de los eventos.

Buenas prácticas para tutores
-------------------------------
- Mantener la contraseña anotada en un lugar seguro y accesible para el tutor.
- Revisar periódicamente que los recordatorios y alarmas están activos.
- Supervisar las primeras interacciones del usuario con la app para evitar confusiones.
- Informes: use la funcionalidad de informes para revisar la actividad semanal o mensual de sus tutorizados y descargue el archivo TXT si necesita archivarlo o compartirlo con familiares/profesionales.

Preguntas frecuentes (solución de problemas)
--------------------------------------------
- No puedo iniciar sesión: comprobar correo/usuario y contraseña; si persiste el problema solicitar restablecimiento de contraseña al tutor.
- Vídeos no aparecen: revisar permisos de acceso a archivos y volver a seleccionar el recurso si es necesario.
- No recibo recordatorios: comprobar que el evento incluye una hora y que la hora no está en el pasado.
