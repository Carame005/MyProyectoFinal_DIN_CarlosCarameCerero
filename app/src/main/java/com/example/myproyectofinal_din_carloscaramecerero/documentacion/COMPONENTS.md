# Componentes Compose reutilizables

Este documento lista los componentes Compose reutilizables del proyecto, su API y usos.

Resumen de componentes clave
----------------------------
- `AppTopBar(user, onSettingsClick, onAvatarChange, currentRoute)`
  - Ubicación: `utils/Componentes.kt`
  - Descripción: Top bar con avatar, acceso a `ProfileMenu`, botón de ajustes y ayuda contextual.
  - Parámetros: `user: User`, `onSettingsClick: () -> Unit`, `onAvatarChange: (Uri) -> Unit`, `currentRoute: String? = null`.

- `AppBottomBar(items, currentRoute, onItemSelected)`
  - Ubicación: `utils/Componentes.kt`
  - Descripción: Barra inferior con navegación entre pantallas. Contiene ahora una entrada adicional para acceder a la pantalla de gestión/visualización de tutorizados (visible para usuarios con rol tutor).
  - Parámetros: `items: List<BottomNavItem>`, `currentRoute: String`, `onItemSelected: (BottomNavItem) -> Unit`.

- `SummaryCard(icon, title, value, modifier = Modifier, onClick = {})`
  - Ubicación: `utils/HomeComponente.kt`
  - Uso: tarjeta resumen en HomeScreen.

- `TaskCard(task, modifier, onClick, onStatusChange, onDelete)`
  - Ubicación: `utils/TareaComponente.kt`
  - Nota: por decisión de permisos, el icono de eliminación (`onDelete`) no se muestra cuando el usuario actual no tiene `isAdmin=true`. El componente acepta el callback pero la UI lo oculta según permisos.

- `AddTaskDialog(onDismiss, onTaskAdded)`
  - Ubicación: `utils/TareaComponente.kt`

- `CollectionCard`, `VideoItemCard`, `VideoPlayerDialog`
  - Ubicación: `utils/VideosComponente.kt`
  - Uso: gestionar colecciones y reproducir vídeos seleccionados.
  - Nota: la reproducción de vídeos embed (YouTube) puede requerir permisos/ajustes WebView; hay un issue conocido (ver TODO).

- `CalendarioGrid(currentMonth, selectedDate, today, events, onDateSelected)`
  - Ubicación: `utils/CalendarioComponente.kt`
  - Uso: grid reutilizable para mostrar calendarios y marcar fechas con eventos.

- `TutorizadosListPreview(userList, onSelectTutorizado)`
  - Ubicación: `pantallas/TutorPantalla.kt`
  - Descripción: Componente que muestra la lista de usuarios disponibles con rol `tutorizado`. Muestra nombre y foto de perfil en una card mínima; si la lista está vacía muestra un mensaje "Aún no hay".
  - Uso: en la pantalla de Tutor permite previsualizar tutorizados.
  - Nota clave: la lógica actual ha cambiado respecto a versiones anteriores: ya no existe botón para agregar o eliminar tutorizados desde la preview; los tutores gestionan (ver/editar/añadir tareas y eventos) todos los usuarios con rol `tutorizado` directamente desde la pantalla de Tutor.

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
- Restricción de borrado: los tutorizados (usuarios con `isAdmin = false`) no pueden eliminar tareas, eventos o vídeos asignados por un tutor —el icono de papelera no se renderiza para ellos. Esto se aplica a nivel UI (componente) y se recomienda reforzarlo también en la capa de persistencia (`AppRepository`) para evitar bypass.

Notas sobre `SettingsDrawer` / Ajustes
------------------------------------
- Se eliminó el interruptor "Función tutor" del drawer de ajustes en la versión actual (decisión de diseño). La opción ya no existe; la aplicaci	n considera el rol y la política centralizada para determinar quién aparece como tutorizado y qué puede gestionar cada usuario.
- Recomendación: mantener la lógica de comprobación de permisos en el `Repository` (no en componentes) para facilitar pruebas y mantener componentes puros.

Buenas prácticas sugeridas
-------------------------
- Añadir `@Preview` para cada componente para facilitar su inspección en IDE.
- Documentar contratos (precondiciones) y efectos secundarios (p. ej. `onAvatarChange` debe persistir avatar fuera del componente).
- Evitar side-effects directos en componentes; delegar al `ViewModel` o repositorio cuando sea necesario.
