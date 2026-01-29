package com.example.myproyectofinal_din_carloscaramecerero

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.MainScaffold
import com.example.myproyectofinal_din_carloscaramecerero.ui.theme.MyProyectoFinal_DIN_CarlosCarameCereroTheme
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.ensureNotificationChannel

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // asegurar canal de notificación en arranque
        ensureNotificationChannel(this)

        // solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                // no bloquear; solo informar en logs
            }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

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
