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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.myproyectofinal_din_carloscaramecerero.utils.* // usar colores desde ColorSheet


val bottomNavItems = listOf(
    BottomNavItem(AppRoute.Tasks.route, Icons.Default.List, "Tareas"),
    BottomNavItem(AppRoute.Calendar.route, Icons.Default.DateRange, "Calendario"),
    BottomNavItem(AppRoute.Home.route, Icons.Default.Home, "Inicio"),
    BottomNavItem(AppRoute.Stats.route, Icons.Default.Check, "Progreso")
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScaffold(
    navController: NavHostController
) {

    var user by remember {
        mutableStateOf(defaultUser)
    }

    // nuevo estado: mostrar login al inicio
    var showLogin by remember { mutableStateOf(true) }

    // nuevo estado: mostrar drawer de ajustes
    var showSettingsDrawer by remember { mutableStateOf(false) }

    // nuevo estado: filtro claro funcional (false = defecto actual; true = filtro claro activo)
    var isLightFilter by remember { mutableStateOf(false) }

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
                        },
                        currentRoute = currentRoute // <-- pasar la ruta actual para ayuda contextual
                    )
                },
                bottomBar = {
                    AppBottomBar(
                        items = bottomNavItems,
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
                        TaskListScreen()
                    }

                    composable(AppRoute.Stats.route) {
                        StatsListScreen()
                    }

                    composable(AppRoute.Settings.route) {
                        // mantenemos por compatibilidad; ahora preferimos drawer, pero si se navega aquí mostramos el drawer abierta
                        SettingsScreen()
                    }
                    composable(AppRoute.Calendar.route) {
                        CalendarioScreen()
                    }
                }
            }

            // Overlay drawer: ahora está fuera del contenido del Scaffold (se pinta encima del topbar y bottombar)
            if (showSettingsDrawer) {
                SettingsDrawer(
                    user = user,
                    onClose = { showSettingsDrawer = false },
                    onChangeName = { newName -> user = user.copy(name = newName) },
                    onChangePassword = { _old, _new -> /* implementar si se desea */ },
                    isLightTheme = isLightFilter,               // pasar estado actual del filtro claro
                    onToggleTheme = { enabled -> isLightFilter = enabled }, // toggle funcional
                    notificationsEnabled = true,
                    onToggleNotifications = { /* implementar */ },
                    onLogout = {
                        showSettingsDrawer = false
                        showLogin = true
                    }
                )
            }
        }
    } // MaterialTheme
}