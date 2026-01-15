package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.net.Uri
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    user: User,
    onSettingsClick: () -> Unit,
    onAvatarChange: (Uri) -> Unit   // 👈 se eleva el evento
) {
    var profileExpanded by remember { mutableStateOf(false) }

    Box {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceGray
            ),
            navigationIcon = {
                IconButton(onClick = { profileExpanded = true }) {
                    // ...reemplazado: mostrar AsyncImage si hay avatarUri, si no fallback a recurso drawable...
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        if (user.avatarUri != null) {
                            AsyncImage(
                                model = user.avatarUri,
                                contentDescription = "Perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(user.avatarRes),
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = IconGray
                    )
                }
            }
        )

        ProfileMenu(
            user = user,
            expanded = profileExpanded,
            onDismiss = { profileExpanded = false },
            onAvatarChange = onAvatarChange // 👈 delega
        )
    }
}



@Composable
fun BottomBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) PrimaryBlue else SurfaceGray
    val iconColor = if (selected) Color.White else IconGray

    Box(
        modifier = Modifier
            .padding(6.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor
        )
    }
}


@Composable
fun AppBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            BottomBarItem(
                item = item,
                selected = item.route == currentRoute,
                onClick = { onItemSelected(item) }
            )
        }
    }
}


