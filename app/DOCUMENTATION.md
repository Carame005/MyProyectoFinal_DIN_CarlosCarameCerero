# MyProyectoFinal — Documentación rápida

## Propósito
Aplicación orientada a soporte para personas con Alzheimer: tareas, calendario con recordatorios, colecciones de vídeo y gestión por perfiles.

## Estructura principal
- `pantallas/` — pantallas composables (Home, Tasks, Calendar, Stats, Settings, Login).
- `utils/` — componentes reutilizables (TopBar, BottomBar, CalendarioComponente, AjustesComponente, ColorSheet...).
- `model/` — modelos de datos (User, Task, CalendarEvent...).
- `repository/` — `AppRepository` responsable de persistencia local por usuario (ficheros JSON privados).
- `res/` — recursos (drawables, strings, colores).

## Persistencia por usuario
- Factor de separación: credenciales del usuario (email/usuario).
- AppRepository guarda/carga archivos JSON privados por usuario (nombres basados en el email saneado).
- Al cambiar de sesión se cargan los datos del perfil actual (usuario, tareas, eventos, colecciones).
- Existe un método de purgado de pruebas para eliminar todos los datos locales.

## Calendario y notificaciones
- `CalendarEvent` contiene id, fecha (LocalDate), título y hora opcional ("HH:mm").
- Si al crear un evento se asigna hora, se programa una alarma (AlarmManager) que lanza un BroadcastReceiver para mostrar la notificación.
- Al eliminar eventos asociados se cancelan las alarmas.

## Vídeos
- Colecciones de vídeo por usuario.
- Selección de vídeo mediante selector de documentos (OpenDocument) y se guarda la Uri persistente (con permiso persistente).
- Reproductor con modo pantalla completa.

## Temas y accesibilidad
- Colores centralizados en `ColorSheet.kt`.
- Switch de tema claro/oscuro afectando toda la app (MaterialTheme dinámico).
- Drawer de ajustes respeta safe drawing area y usa los colores de la app.
- UI orientada a accesibilidad: textos envolventes, tarjetas verticales, botones grandes.

## Desarrollo y pruebas
- Para pruebas hay botón en login para purgar todos los datos.
- Para añadir nuevas pantallas o persistir nuevos modelos: añadir data class en `model/` y funciones en `AppRepository`.

## Cómo localizar cosas rápidamente
- Persistencia: `repository/appRepo.kt`
- Calendario: `pantallas/CalendarioPantalla.kt` + `utils/CalendarioComponente.kt`
- Ajustes / Drawer: `utils/AjustesComponente.kt`
- Home: `pantallas/HomePantalla.kt`
- Login: `pantallas/LoginPantalla.kt`

## Notas finales
- Evitar cambiar nombres de claves SharedPreferences/ficheros si se quiere mantener compatibilidad con datos existentes.
- Para soporte futuro (sincronización) se recomienda normalizar modelos en `model/` y usar `AppRepository` como único punto de acceso a disco.

