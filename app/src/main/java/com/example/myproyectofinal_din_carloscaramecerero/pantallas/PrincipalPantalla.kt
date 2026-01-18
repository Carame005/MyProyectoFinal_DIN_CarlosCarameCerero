package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myproyectofinal_din_carloscaramecerero.model.AppRoute
import com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem
import com.example.myproyectofinal_din_carloscaramecerero.utils.AppBottomBar
import com.example.myproyectofinal_din_carloscaramecerero.utils.AppTopBar
import com.example.myproyectofinal_din_carloscaramecerero.utils.defaultUser


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

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    // Si showLogin == true, mostrar solo la pantalla de login (sin topbar/bottombar)
    if (showLogin) {
        LoginScreen(onLogin = { newUser ->
            user = newUser
            showLogin = false
        })
        return
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            AppTopBar(
                user = user,
                onSettingsClick = { /* navegar ajustes */ },
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
                SettingsScreen()
            }
            composable(AppRoute.Calendar.route) {
                CalendarioScreen()
            }
        }
    }
}