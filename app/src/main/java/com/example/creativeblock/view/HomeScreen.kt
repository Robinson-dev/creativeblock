package com.example.creativeblock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.creativeblock.data.Idea
import com.example.creativeblock.ui.components.AppTopBar
import com.example.creativeblock.viewmodel.IdeaViewModel

// Pantalla principal que muestra la lista de ideas creativas
// Diseño actualizado con barra de búsqueda redondeada y tarjetas más suaves
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: IdeaViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onToggleTheme: () -> Unit,     // 🆕 nuevo
    isDarkTheme: Boolean
) {
    // Observa la lista de ideas desde el ViewModel
    val ideas by viewModel.allIdeas.collectAsState(initial = emptyList())

    // Estado local para el texto de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Filtra las ideas según el texto ingresado en la búsqueda
    val filteredIdeas = ideas.filter { idea ->
        searchQuery.isEmpty() ||
                idea.titulo.contains(searchQuery, ignoreCase = true) ||
                idea.descripcion.contains(searchQuery, ignoreCase = true) ||
                idea.categoria.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "CreativeBlock",
                onToggleTheme = onToggleTheme, // 🔦 le pasas el callback
                isDarkTheme = isDarkTheme      // 🔦 y el estado actual
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar idea")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🧭 BARRA DE BÚSQUEDA MODERNA
            // Reemplazamos el TextField simple por uno con bordes redondeados y sombra suave
            // 🧭 BARRA DE BÚSQUEDA MODERNA SIN LÍNEA INFERIOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(2.dp, shape = RoundedCornerShape(16.dp)), // sombra suave
                shape = RoundedCornerShape(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    placeholder = {
                        Text(
                            text = "Buscar ideas por título, descripción o categoría...",
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),

                    // 🎯 CAMBIO CLAVE: todos los indicadores a transparente → sin línea inferior
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
            }


            // 🔍 MUESTRA RESULTADOS
            if (filteredIdeas.isEmpty()) {
                // Si no hay ideas, muestra mensaje informativo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (searchQuery.isNotEmpty()) {
                        // Cuando hay búsqueda pero sin resultados
                        Text(
                            text = "No se encontraron ideas",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prueba con otros términos o revisa tu ortografía",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Cuando no hay ideas creadas aún
                        Text(
                            text = "¡Aún no tienes ideas!",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Presiona el botón + para crear tu primera idea creativa",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // 📊 Muestra cantidad de resultados cuando hay búsqueda activa
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "Encontradas ${filteredIdeas.size} ideas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // LISTADO DE IDEAS
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredIdeas) { idea ->
                        // Cada tarjeta representa una idea
                        ModernIdeaCard(
                            idea = idea,
                            onClick = { onNavigateToDetail(idea.id) }
                        )
                    }
                }
            }
        }
    }
}

// 🪶 TARJETA DE IDEA MODERNA
// Se actualiza el estilo de la tarjeta con esquinas suaves y separación visual más atractiva
@Composable
fun ModernIdeaCard(
    idea: Idea,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(16.dp)), // sombra sutil
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 🏷️ Título principal
            Text(
                text = idea.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🧾 Descripción (limitada a 100 caracteres)
            val descripcionCorta = if (idea.descripcion.length > 100) {
                idea.descripcion.substring(0, 100) + "..."
            } else {
                idea.descripcion
            }

            Text(
                text = descripcionCorta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🧩 Información adicional (categoría y estado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Categoría
                Text(
                    text = idea.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Estado
                Text(
                    text = idea.estado,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
