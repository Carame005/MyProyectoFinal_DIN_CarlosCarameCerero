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
  - Descripción: Barra inferior con navegación entre pantallas.
  - Parámetros: `items: List<BottomNavItem>`, `currentRoute: String`, `onItemSelected: (BottomNavItem) -> Unit`.

- `SummaryCard(icon, title, value, modifier = Modifier, onClick = {})`
  - Ubicación: `utils/HomeComponente.kt`
  - Uso: tarjeta resumen en HomeScreen.

- `TaskCard(task, modifier, onClick, onStatusChange, onDelete)`
  - Ubicación: `utils/TareaComponente.kt`
  - Uso: visualización y acciones sobre tareas.

- `AddTaskDialog(onDismiss, onTaskAdded)`
  - Ubicación: `utils/TareaComponente.kt`

- `CollectionCard`, `VideoItemCard`, `VideoPlayerDialog`
  - Ubicación: `utils/VideosComponente.kt`
  - Uso: gestionar colecciones y reproducir vídeos seleccionados.

- `CalendarioGrid(currentMonth, selectedDate, today, events, onDateSelected)`
  - Ubicación: `utils/CalendarioComponente.kt`
  - Uso: grid reutilizable para mostrar calendarios y marcar fechas con eventos.

- `TutorizadosListPreview(userList, onSelectTutorizado)`
  - Ubicación: `pantallas/TutorPantalla.kt` (UI principal del tutor)
  - Descripción: Componente que muestra la lista de usuarios que pueden ser añadidos como tutorizados. Muestra nombre y foto de perfil en una card mínima; si la lista está vacía muestra un mensaje "Aún no hay".
  - Uso: en la pantalla de Tutor permite previsualizar y seleccionar usuarios (solo usuarios no-admin) para agregarlos como tutorizados.

- `TutorizadoCard(user, isAdded, expanded, onAdd, onRemove, onExpandChange, expandedContent)`
  - Ubicación: `utils/TutorComponente.kt`
  - Descripción: Card reutilizable que muestra avatar, nombre y email del usuario, con un botón para agregar/eliminar de la lista de tutorizados. Soporta estado expandible y acepta un slot `expandedContent` para renderizar tareas/eventos.
  - Parámetros: `user: User`, `isAdded: Boolean`, `expanded: Boolean`, `onAdd: () -> Unit`, `onRemove: () -> Unit`, `onExpandChange: (Boolean) -> Unit`, `expandedContent: (@Composable () -> Unit)?`

- `TutorScreen(tutorEmail)`
  - Ubicación: `pantallas/TutorPantalla.kt`
  - Descripción: Pantalla completa para tutores con listado de usuarios (filtrados por `allowTutoring` y no-admin), manejo de tutorizados (añadir/quitar) y edición de tareas/eventos de cada tutorizado.
  - Parámetros: `tutorEmail: String` (email del tutor actual).

Notas sobre `SettingsDrawer` / Ajustes
------------------------------------
- El `SettingsDrawer` ahora incluye un interruptor "Función tutor" que controla si el usuario acepta ser seleccionado por un tutor. Al desactivar el switch el componente consulta `AppRepository.isTutorizadoByAny(context, user.email)` y, si existe alguna referencia, muestra un diálogo de error y bloquea la desactivación.
- Recomendación: mantener la lógica de comprobación en el `Repository` (no en componentes) para facilitar pruebas y mantener componentes puros.

Buenas prácticas sugeridas
-------------------------
- Añadir `@Preview` para cada componente para facilitar su inspección en IDE.
- Documentar contratos (precondiciones) y efectos secundarios (p. ej. `onAvatarChange` debe persistir avatar fuera del componente).
- Evitar side-effects directos en componentes; delegar al `ViewModel` o repositorio cuando sea necesario.
