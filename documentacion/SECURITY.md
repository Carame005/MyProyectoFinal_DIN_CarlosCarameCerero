# Seguridad: riesgos detectados y medidas propuestas

Resumen
-------
Este documento recoge los riesgos detectados en el proyecto y las medidas propuestas para mitigarlos, con prioridad en la gestión de credenciales y el manejo de excepciones.

Riesgos detectados
------------------
1. Credenciales en texto plano
   - Ubicación: `repository/AppRepository.saveCredentials` / `loadCredentials` (archivo `*_creds.json`).
   - Riesgo: exposición de contraseñas si el dispositivo es comprometido o si el backup incluye archivos de la app.

2. Catch silenciosos
   - En `AppRepository` y otros archivos existen `catch (_: Exception) {}` que silencian y ocultan errores. Esto dificulta depuración y oculta fallos.

3. Permisos y notificaciones
   - `CalendarioPantalla` programa alarmas y crea notificaciones; en Android 13 (API 33) se requiere permiso `POST_NOTIFICATIONS`. No hay comprobación explícita ni solicitud de permiso en el flujo.

4. Acceso a URIs persistentes
   - `VideosPantalla` usa `takePersistableUriPermission`, pero no hay manejo explícito si el permiso se revoca o si el URI deja de ser válido.

Medidas propuestas (priorizadas)
--------------------------------
1. Migración de credenciales a almacenamiento seguro (alta prioridad)
   - Opciones: `EncryptedSharedPreferences` (AndroidX Security) o almacenar hash+salt de contraseñas si se mantiene autenticación local.
   - Plan de migración: detectar credenciales en texto plano y migrarlas al primer inicio tras desplegar el cambio.

2. Evitar catches silenciosos (alta prioridad)
   - Registrar errores con `Log.e(TAG, "message", ex)` o propagar excepciones donde proceda para facilitar debugging.

3. Manejo de permisos (media)
   - Solicitar `POST_NOTIFICATIONS` antes de crear notificaciones en Android 13+.
   - Manejar permisos de lectura de URIs de forma robusta (comprobar `persistedUriPermissions`).

4. Pruebas y análisis de seguridad (media)
   - Integrar análisis estático (Detekt, ktlint, SpotBugs) y, si procede, análisis dinámico (MobSF).

5. Minimizar datos sensibles en backups (media)
   - Si no se desea incluir ficheros sensibles en backups, marcar `android:allowBackup="false"` en `AndroidManifest.xml` o excluir ficheros concretos.

Migración de credenciales (alto nivel)
-------------------------------------
- Descripción: implementar `migrateCredentialsIfNeeded(ctx)` que detecte `*_creds.json`, lea la contraseña, la almacene en `EncryptedSharedPreferences` y borre el fichero en texto plano.

Prioridad de acciones
---------------------
- Alta prioridad: migración de credenciales y corrección de catches silenciosos.
- Media prioridad: manejo de permisos de notificación y URIs persistentes.
- Baja prioridad: integración de herramientas de análisis avanzado.

