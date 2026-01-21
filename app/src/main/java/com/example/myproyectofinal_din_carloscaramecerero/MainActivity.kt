package com.example.myproyectofinal_din_carloscaramecerero

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.MainScaffold
import com.example.myproyectofinal_din_carloscaramecerero.ui.theme.MyProyectoFinal_DIN_CarlosCarameCereroTheme

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AutiCareApp() {
    val navController = rememberNavController()
    MainScaffold(navController = navController)
}
