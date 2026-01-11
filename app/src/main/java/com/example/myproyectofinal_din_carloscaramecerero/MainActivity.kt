package com.example.myproyectofinal_din_carloscaramecerero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.MainScaffold
import com.example.myproyectofinal_din_carloscaramecerero.ui.theme.MyProyectoFinal_DIN_CarlosCarameCereroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyProyectoFinal_DIN_CarlosCarameCereroTheme {
                AutiCareApp()
            }
        }
    }
}

@Composable
fun AutiCareApp() {
    val navController = rememberNavController()
    MainScaffold(navController = navController)
}


