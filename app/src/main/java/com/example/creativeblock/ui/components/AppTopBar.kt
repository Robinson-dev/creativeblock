package com.example.creativeblock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// 🧭 Barra superior principal personalizada
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationIcon: ImageVector? = null,
    navigationIconDescription: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    onToggleTheme: (() -> Unit)? = null,   // 🆕 Función para alternar tema
    isDarkTheme: Boolean = false           // 🆕 Estado del tema actual
) {
    // 🎨 Color sólido gris claro (sin gradiente)
    val topBarColor = Color(0xFFF2F2F2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(topBarColor)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
            },
            navigationIcon = {
                if (navigationIcon != null && onNavigationClick != null) {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = navigationIconDescription,
                            tint = Color.Black
                        )
                    }
                }
            },
            actions = {
                // 🔦 Botón de tema claro/oscuro
                if (onToggleTheme != null) {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme)
                                Icons.Default.Brightness7   // ☀️ modo claro
                            else
                                Icons.Default.Brightness4,  // 🌙 modo oscuro
                            contentDescription = "Cambiar tema",
                            tint = Color.Black
                        )
                    }
                }

                // Acciones adicionales que pueda recibir
                actions()
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            windowInsets = WindowInsets.statusBars
        )
    }
}

// 🔙 Variante con botón de volver y soporte para cambio de tema
@Composable
fun AppTopBarWithBack(
    title: String,
    onBackClick: () -> Unit,
    onToggleTheme: (() -> Unit)? = null,   // 🆕 callback del botón de tema
    isDarkTheme: Boolean = false,          // 🆕 estado actual del tema
    actions: @Composable () -> Unit = {}
) {
    AppTopBar(
        title = title,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        navigationIconDescription = "Volver",
        onNavigationClick = onBackClick,
        actions = actions,
        onToggleTheme = onToggleTheme,     // ✅ se pasa a la barra base
        isDarkTheme = isDarkTheme
    )
}
