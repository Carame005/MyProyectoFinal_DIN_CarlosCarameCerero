package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myproyectofinal_din_carloscaramecerero.model.AppRoute
import com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem
import com.example.myproyectofinal_din_carloscaramecerero.utils.AppBottomBar
import com.example.myproyectofinal_din_carloscaramecerero.utils.AppTopBar
import com.example.myproyectofinal_din_carloscaramecerero.utils.SettingsDrawer
import com.example.myproyectofinal_din_carloscaramecerero.utils.SettingsScreen
import com.example.myproyectofinal_din_carloscaramecerero.utils.defaultUser
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.LoginScreen // <-- nuevo import
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddButtonBlue
import com.example.myproyectofinal_din_carloscaramecerero.utils.DarkBackground
import com.example.myproyectofinal_din_carloscaramecerero.utils.LightFilterBackground
import com.example.myproyectofinal_din_carloscaramecerero.utils.LightFilterSurface
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository // <-- nuevo

/**
 * Elementos de la barra de navegación inferior usados en la [MainScaffold].
 */
val bottomNavItems = listOf(
    BottomNavItem(AppRoute.Tasks.route, Icons.Default.List, "Tareas"),
    BottomNavItem(AppRoute.Calendar.route, Icons.Default.DateRange, "Calendario"),
    BottomNavItem(AppRoute.Home.route, Icons.Default.Home, "Inicio"),
    BottomNavItem(AppRoute.Stats.route, Icons.Default.Check, "Progreso")
)

/**
 * Scaffold principal de la aplicación. Gestiona:
 *  - sesión de usuario (login/logout),
 *  - tema (filtro claro/oscuro) por usuario,
 *  - navegación entre pantallas y la superposición del drawer de ajustes.
 *
 * @param navController NavController usado para navegar entre rutas.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScaffold(
    navController: NavHostController
) {
    var user by remember {
        mutableStateOf(defaultUser)
    }

    val ctx = LocalContext.current // <-- contexto para AppRepository y prefs

    // nuevo estado: mostrar login al inicio
    var showLogin by remember { mutableStateOf(true) }

    // nuevo estado: mostrar drawer de ajustes
    var showSettingsDrawer by remember { mutableStateOf(false) }

    // nuevo estado: filtro claro funcional (false = defecto actual; true = filtro claro activo)
    var isLightFilter by remember { mutableStateOf(false) }

    // Si debe mostrarse el login, mostrarlo y devolver temprano (evitamos renderizar Scaffold/drawer)
    if (showLogin) {
        LoginScreen(onLogin = { newUser ->
            // intentar cargar usuario previamente guardado; si existe lo usamos, si no guardamos el nuevo
            val stored = AppRepository.loadUser(ctx, newUser.email)
            if (stored != null) {
                user = stored
            } else {
                user = newUser
                AppRepository.saveUser(ctx, user)
            }
            // cargar preferencia de tema del usuario
            val prefsU = ctx.getSharedPreferences("user_prefs_${user.email}", android.content.Context.MODE_PRIVATE)
            isLightFilter = prefsU.getBoolean("theme_light", false)
            showLogin = false
        })
        return
    }

    // cuando cambie usuario guardamos/restauramos preferencia de tema por usuario
    LaunchedEffect(user.email) {
        if (user.email.isNotBlank()) {
            val prefs = ctx.getSharedPreferences("user_prefs_${user.email}", android.content.Context.MODE_PRIVATE)
            isLightFilter = prefs.getBoolean("theme_light", isLightFilter)
        }
    }
    LaunchedEffect(isLightFilter, user.email) {
        if (user.email.isNotBlank()) {
            val prefs = ctx.getSharedPreferences("user_prefs_${user.email}", android.content.Context.MODE_PRIVATE)
            prefs.edit().putBoolean("theme_light", isLightFilter).apply()
        }
    }

    // obtener la ruta actual del NavController (necesaria por AppTopBar / AppBottomBar)
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    // color scheme dinámico según isLightFilter
    val appColorScheme = if (isLightFilter) {
        lightColorScheme(
            primary = AddButtonBlue,
            surface = LightFilterSurface,
            background = LightFilterBackground,
            onSurface = Color.Black,
            onPrimary = Color.White
        )
    } else {
        darkColorScheme(
            primary = AddButtonBlue,
            surface = DarkBackground,
            background = DarkBackground,
            onSurface = Color.White,
            onPrimary = Color.White
        )
    }

    MaterialTheme(colorScheme = appColorScheme) {
        // envolvemos todo en un Box para poder superponer el drawer sobre topbar/bottombar
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                 modifier = Modifier.safeDrawingPadding(),
                 topBar = {
                     AppTopBar(
                         user = user,
                         onSettingsClick = { showSettingsDrawer = true }, // abrir drawer en lugar de navegar
                         onAvatarChange = { uri ->
                             user = user.copy(avatarUri = uri)
                             AppRepository.saveUser(ctx, user) // persistir cambio avatar inmediatamente
                         },
                         currentRoute = currentRoute // <-- pasar la ruta actual para ayuda contextual
                     )
                 },
                bottomBar = {
                    // construir items dinámicamente: incluir Tutor solo si user.esAdmin
                    val itemsLocal = remember(user.esAdmin) {
                        val base = listOf(
                            BottomNavItem(AppRoute.Tasks.route, Icons.Default.List, "Tareas"),
                            BottomNavItem(AppRoute.Calendar.route, Icons.Default.DateRange, "Calendario"),
                            BottomNavItem(AppRoute.Home.route, Icons.Default.Home, "Inicio"),
                            BottomNavItem(AppRoute.Stats.route, Icons.Default.Check, "Progreso")
                        ).toMutableList()
                        if (user.esAdmin) {
                            base.add(BottomNavItem(AppRoute.Tutor.route, Icons.Default.People, "Tutor"))
                        }
                        base.toList()
                    }

                    AppBottomBar(
                        items = itemsLocal,
                        currentRoute = currentRoute ?: AppRoute.Home.route,
                        onItemSelected = { item ->
                            navController.navigate(item.route) {
                                popUpTo(AppRoute.Home.route)
                                launchSingleTop = true
                            }
                        }
                    )
                }
             ) { padding ->
                // NavHost dentro del contenido del Scaffold
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Home.route,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(AppRoute.Home.route) {
                        HomeScreen(user)
                    }

                    composable(AppRoute.Tasks.route) {
                        TaskListScreen(user.email) // <-- pasar email para persistencia por perfil
                    }

                    composable(AppRoute.Stats.route) {
                        StatsListScreen(user.email) // <-- pasar email
                    }

                    composable(AppRoute.Settings.route) {
                        // mantenemos por compatibilidad; ahora preferimos drawer, pero si se navega aquí mostramos el drawer abierta
                        SettingsScreen()
                    }
                    composable(AppRoute.Calendar.route) {
                        CalendarioScreen(user.email) // <-- pasar email
                    }
                    composable(AppRoute.Tutor.route) {
                        TutorScreen(user.email)
                    }
                 }
             }

            // Overlay drawer: ahora está fuera del contenido del Scaffold (se pinta encima del topbar y bottombar)
            if (showSettingsDrawer) {
                SettingsDrawer(
                    user = user,
                    onClose = { showSettingsDrawer = false },
                    onChangeName = { newName ->
                        user = user.copy(name = newName)
                        AppRepository.saveUser(ctx, user) // guardar cambios de usuario por perfil
                    },
                    onChangePassword = { _old, _new -> /* implementar si se desea */ },
                    isLightTheme = isLightFilter,               // pasar estado actual del filtro claro
                    onToggleTheme = { enabled -> isLightFilter = enabled }, // toggle funcional
                    notificationsEnabled = true,
                    onToggleNotifications = { /* implementar */ },
                    onLogout = {
                        showSettingsDrawer = false
                        // opcional: limpiar memoria/volver a login
                        showLogin = true
                        // navegar a Home y limpiar backstack para evitar que la próxima sesión herede la ruta anterior
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo(AppRoute.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                 )
             }
         }
     } // MaterialTheme
 }
