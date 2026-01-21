# TODO / Siguientes pasos recomendados

Prioridad alta
--------------
- Migrar almacenamiento de credenciales a `EncryptedSharedPreferences`.
- Añadir logging y evitar `catch` silenciosos en `AppRepository`.
- Documentar formatos JSON (PERSISTENCE.md ya creado) y flujos de login/migración.
- Añadir pruebas unitarias para serialización/deserialización.

Prioridad media
---------------
- Añadir Compose Previews para componentes reutilizables.
- Añadir CI (build + tests) con GitHub Actions.
- Solicitar permiso `POST_NOTIFICATIONS` en tiempo de ejecución cuando sea necesario.
- Implementar y documentar `TutorPantalla` (gestión de tutores/tutorizados, ver/añadir tareas y eventos para tutorizados). Documentar el flujo en RA5.
- Revisar y corregir bug: avatars no cargan correctamente tras cerrar sesión y volver a iniciar (posible problema con URIs persistentes o caching de imágenes).
- Corregir icono duplicado de app para el rol tutor (asegurar recursos de iconos únicos por rol donde corresponda).
- Revisar bug: al cerrar sesión desde pantalla de tutorizados e iniciar sesión como tutorizado, el usuario aparece en la lista de tutorizados; investigar y arreglar condición de filtrado/estado al limpiar sesión.

Prioridad baja
--------------
- Refactorizar persistencia a Room y separar interfaz de repositorio.
- Añadir tutoriales y manual de usuario.
- Implementar NUI (voz/gesto) como propuesta de futura mejora.

Si quieres que implemente alguno de estos puntos automáticamente, dime cuál y lo hago.
