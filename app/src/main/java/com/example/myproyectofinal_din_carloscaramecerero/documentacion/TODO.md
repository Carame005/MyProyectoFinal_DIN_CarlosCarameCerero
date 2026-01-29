# TODO / Siguientes pasos recomendados

Prioridad alta
--------------
- Migrar almacenamiento de credenciales a `EncryptedSharedPreferences`.
- Añadir logging y evitar `catch` silenciosos en `AppRepository`.
- Documentar formatos JSON (PERSISTENCE.md ya creado) y flujos de login/migración.
- Añadir pruebas unitarias para serialización/deserialización.
- Implementar validación al crear vídeos: no permitir elegir archivo local y URL al mismo tiempo; mostrar error y bloquear creación.

Prioridad media
---------------
- Añadir Compose Previews para componentes reutilizables.
- Añadir CI (build + tests) con GitHub Actions.
- Solicitar permiso `POST_NOTIFICATIONS` en tiempo de ejecución cuando sea necesario.
- Implementar y documentar `TutorPantalla` (gestión centralizada de usuarios con rol `tutorizado`, ver/añadir tareas, eventos y colecciones de vídeos para cada tutorizado). Eliminar el botón de "añadir tutorizado" y el interruptor "Función tutor" del drawer: los tutores gestionan todos los usuarios marcados como `tutorizado`.
- Añadir opción de "Inicio rápido" con selección por cuenta y autenticación biométrica (si el dispositivo lo soporta).
- Revisar y corregir bug: avatars no cargan correctamente tras cerrar sesión y volver a iniciar (posible problema con URIs persistentes o caching de imágenes).
- Corregir icono duplicado de app para el rol tutor (asegurar recursos de iconos únicos por rol donde corresponda).
- Impedir que los usuarios no-admin (tutorizados) eliminen tareas/eventos/vídeos creados por tutores: ocultar icono de papelera y reforzarlo en `AppRepository`.

Prioridad baja
--------------
- Refactorizar persistencia a Room y separar interfaz de repositorio.
- Añadir tutoriales y manual de usuario.
- Implementar NUI (voz/gesto) como propuesta de futura mejora.
- Implementar migración de credenciales desde `*_creds.json` a `EncryptedSharedPreferences`.

Si quieres que implemente alguno de estos puntos automáticamente, dime cuál y lo hago.
