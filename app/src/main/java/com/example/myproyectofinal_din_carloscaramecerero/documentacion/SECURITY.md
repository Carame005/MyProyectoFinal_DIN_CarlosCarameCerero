# Seguridad: riesgos detectados y recomendaciones

Resumen rápido
--------------
He detectado varios riesgos y áreas de mejora en el código actual, especialmente en la gestión de credenciales y en el manejo silencioso de excepciones.

Riesgos detectados
------------------
1. Credenciales en texto plano
   - Ubicación: `repository/AppRepository.saveCredentials` / `loadCredentials` (archivo `*_creds.json`).
   - Riesgo: exposición de contraseñas si el dispositivo es comprometido o si el backup incluye archivos de la app.

2. Catch silenciosos
   - En `AppRepository` y otros archivos hay `catch (_: Exception) {}` que silencian y ocultan errores. Esto dificulta depuración y oculta fallos.

3. Permisos y notificaciones
   - `CalendarioPantalla` programa alarmas y crea notificaciones; desde Android 13 (API 33) se requiere permiso `POST_NOTIFICATIONS`. No hay comprobación explícita ni solicitud de permiso en el flujo.

4. Acceso a URIs persistentes
   - `VideosPantalla` usa `takePersistableUriPermission`, pero no hay manejo explícito si el permiso se revoca o si el URI deja de ser válido.

Recomendaciones (priorizadas)
-----------------------------
1. Migrar credenciales a almacenamiento seguro (alta prioridad)
   - Uso recomendado: `EncryptedSharedPreferences` (AndroidX Security) o almacenar hash+salt de contraseñas si se usa autenticación local.
   - Plan de migración: detectar credenciales en texto plano y migrarlas al primer inicio tras desplegar el cambio.

2. Evitar catch silenciosos (alta prioridad)
   - Al menos loggear errores con `Log.e(TAG, "message", ex)` o propagar excepciones donde proceda. Esto ayudará durante debugging y QA.

3. Manejo de permisos (media)
   - Solicitar `POST_NOTIFICATIONS` antes de crear notificaciones en Android 13+.
   - Manejar permisos de lectura de URIs de forma robusta (comprobar `persistedUriPermissions`).

4. Pruebas de seguridad (media)
   - Añadir análisis estático (Detekt, ktlint, SpotBugs) y dinámico si es posible (MobSF).

5. Minimizar datos sensibles en backups
   - Si no se quiere que los ficheros de la app se incluyan en backups, marcar `android:allowBackup="false"` en el `AndroidManifest.xml` o excluir ficheros sensibles.

Ejemplo de migración sugerida (alto nivel)
------------------------------------------
- En la nueva versión, `saveCredentials` guardará la contraseña en `EncryptedSharedPreferences`.
- Añadir `migrateCredentialsIfNeeded(ctx)` que al inicio compruebe si existe `*_creds.json`; si existe, lee y escribe de forma segura en `EncryptedSharedPreferences` y borra el fichero en texto plano.

Si quieres, implemento la migración y la actualización de `AppRepository` ahora (necesitaré permiso para editar el archivo).
