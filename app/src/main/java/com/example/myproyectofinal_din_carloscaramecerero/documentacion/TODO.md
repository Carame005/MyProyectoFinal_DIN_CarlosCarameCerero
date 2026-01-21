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

Prioridad baja
--------------
- Refactorizar persistencia a Room y separar interfaz de repositorio.
- Añadir tutoriales y manual de usuario.
- Implementar NUI (voz/gesto) como propuesta de futura mejora.

Si quieres que implemente alguno de estos puntos automáticamente, dime cuál y lo hago.
