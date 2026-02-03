# Componentes Compose reutilizables (RA3)

Este documento lista los componentes Compose reutilizables del proyecto, su API y usos. El objetivo es describir su comportamiento, parámetros y restricciones relevantes para la evaluación RA3.

Resumen de componentes clave
----------------------------
- `AppTopBar(user, onSettingsClick, onAvatarChange, currentRoute)`
  - Ubicación: `utils/Componentes.kt`
  - Descripción: Top bar con avatar, acceso a `ProfileMenu`, botón de ajustes y ayuda contextual.
  - Parámetros: `user: User`, `onSettingsClick: () -> Unit`, `onAvatarChange: (Uri) -> Unit`, `currentRoute: String? = null`.

- `AppBottomBar(items, currentRoute, onItemSelected)`
  - Ubicación: `utils/Componentes.kt`
  - Descripción: Barra inferior con navegación entre pantallas. Contiene una entrada para acceder a la pantalla de gestión/visualización de tutorizados (visible para usuarios con rol tutor).
  - Parámetros: `items: List<BottomNavItem>`, `currentRoute: String`, `onItemSelected: (BottomNavItem) -> Unit`.

- `SummaryCard(icon, title, value, modifier = Modifier, onClick = {})`
  - Ubicación: `utils/HomeComponente.kt`
  - Uso: tarjeta resumen en HomeScreen.

- `TaskCard(task, modifier, onClick, onStatusChange, onDelete)`
  - Ubicación: `utils/TareaComponente.kt`
  - Nota: el icono de eliminación (`onDelete`) no se muestra cuando el usuario actual no tiene `isAdmin=true`. El componente acepta el callback pero la UI lo oculta según permisos.

- `AddTaskDialog(onDismiss, onTaskAdded)`
  - Ubicación: `utils/TareaComponente.kt`

- `CollectionCard`, `VideoItemCard`, `VideoPlayerDialog`
  - Ubicación: `utils/VideosComponente.kt`
  - Uso: gestionar colecciones y reproducir vídeos seleccionados.
  - Nota: la reproducción de vídeos embed (YouTube) puede requerir ajustes de `WebView` y permisos; existe un issue conocido relativo a la carga.

- `CalendarioGrid(currentMonth, selectedDate, today, events, onDateSelected)`
  - Ubicación: `utils/CalendarioComponente.kt`
  - Uso: grid reutilizable para mostrar calendarios y marcar fechas con eventos.

- `TutorizadosListPreview(userList, onSelectTutorizado)`
  - Ubicación: `pantallas/TutorPantalla.kt`
  - Descripción: Componente que muestra la lista de usuarios disponibles con rol `tutorizado`. Muestra nombre y foto de perfil en una card mínima; si la lista está vacía muestra el mensaje "Aún no hay".
  - Uso: en la pantalla de Tutor permite previsualizar tutorizados.
  - Comportamiento actual: la gestión de tutorizados (agregar/eliminar) se realiza de forma centralizada en la pantalla de Tutor; la preview solo muestra y permite navegación.

- `TutorizadoCard(user, expanded, onExpandChange, expandedContent)`
  - Ubicación: `utils/TutorComponente.kt`
  - Descripción: Card reutilizable que muestra avatar, nombre y email del usuario, con soporte expandible. En la versión actual la card no contiene botones para añadir/quitar tutorizados (esa gestión está centralizada). Soporta un slot `expandedContent` para renderizar tareas/eventos y acciones permitidas.
  - Parámetros: `user: User`, `expanded: Boolean`, `onExpandChange: (Boolean) -> Unit`, `expandedContent: (@Composable () -> Unit)?`

- `TutorScreen(tutorEmail)`
  - Ubicación: `pantallas/TutorPantalla.kt`
  - Descripción: Pantalla completa para tutores con listado de usuarios (filtrados por rol `tutorizado`), visualización en cards expandibles y edición/creación de tareas, eventos y colecciones de vídeos para cada tutorizado.
  - Parámetros: `tutorEmail: String` (email del tutor actual).

Notas sobre permisos y UI
------------------------
- Restricción de borrado: los tutorizados (usuarios con `isAdmin = false`) no pueden eliminar tareas, eventos o vídeos asignados por un tutor —el icono de papelera no se renderiza para ellos. Esta restricción se aplica a nivel UI y se recomienda que también esté presente en la capa de persistencia (`AppRepository`) para evitar bypass.

Notas sobre `SettingsDrawer` / Ajustes
------------------------------------
- El interruptor "Función tutor" fue eliminado del drawer de ajustes en la versión documentada. La aplicación determina el acceso a funciones de tutor mediante el rol y políticas centralizadas.
