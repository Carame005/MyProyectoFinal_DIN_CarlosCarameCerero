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

Buenas prácticas sugeridas
-------------------------
- Añadir `@Preview` para cada componente para facilitar su inspección en IDE.
- Documentar contratos (precondiciones) y efectos secundarios (p. ej. `onAvatarChange` debe persistir avatar fuera del componente).
- Evitar side-effects directos en componentes; delegar al `ViewModel` o repositorio cuando sea necesario.
