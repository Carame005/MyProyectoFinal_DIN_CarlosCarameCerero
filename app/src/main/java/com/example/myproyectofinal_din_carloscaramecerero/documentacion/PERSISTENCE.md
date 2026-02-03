# Persistencia de datos(RA6)

Resumen
-------
La aplicación persiste datos localmente por usuario usando ficheros JSON en el almacenamiento interno de la app (`Context.openFileOutput` / `openFileInput`). La lógica está centralizada en `repository/AppRepository`.

Ficheros y sufijos
------------------
Cada usuario tiene ficheros con sufijos específicos (nombre generado por `fileNameFor(userEmail, suffix)`):

- `*_user.json` — datos del usuario (name, email, avatarRes, avatarUri, esAdmin)
  - Campos adicionales importantes:
    - `allowTutoring` (boolean): indica si el usuario acepta ser seleccionado como tutorizado por un tutor. Está persisitido junto al resto de datos del usuario en `*_user.json`.
- `*_tasks.json` — lista de tareas: array de objetos {id, title, description, status}
- `*_events.json` — lista de eventos: array de objetos {id, date (ISO), title, time}
- `*_collections.json` — colecciones de vídeos: array con items (id, title, description, uriString)
- `*_creds.json` — credenciales (password) (IMPORTANTE: actualmente en texto plano)

Formato JSON (ejemplos)
-----------------------
- Usuario:
```json
{
  "name": "Juan",
  "email": "juan@example.com",
  "avatarRes": 2131230890,
  "avatarUri": "content://...",
  "esAdmin": false
}
```

- Task array:
```json
[ { "id": 123, "title":"Comprar", "description":"Leche", "status":"PENDING" } ]
```

Acceso y nombres de fichero
---------------------------
- Los nombres se generan reemplazando caracteres no alfanuméricos por `_` en el email: por ejemplo `juan_example_com_user.json`.
- `AppRepository` encapsula la lectura/escritura y devuelve listas vacías o null en caso de no existir el fichero.

Migración y recomendaciones
---------------------------
- Migrar credenciales a `EncryptedSharedPreferences` y planificar migración de datos existente al primer inicio.
- Nota sobre preferencias de usuario:
  - Cuando el usuario cambia la opción "Función tutor" en Ajustes, `AppRepository.saveUser` debe actualizar el campo `allowTutoring` en el `*_user.json`.
  - Al desactivar la opción la UI comprueba primero `AppRepository.isTutorizadoByAny(context, email)` para evitar inconsistencia si existe una relación tutor->tutorizado.
- Considerar Room para consultas más complejas y relaciones entre entidades.
- Añadir pruebas unitarias para la serialización/deserialización (JSON <-> modelos).
