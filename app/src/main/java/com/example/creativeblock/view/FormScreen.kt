package com.example.creativeblock.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creativeblock.model.Categoria
import com.example.creativeblock.model.Estado
import com.example.creativeblock.model.Prioridad
import com.example.creativeblock.ui.components.AppTopBarWithBack
import com.example.creativeblock.viewmodel.IdeaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: IdeaViewModel,
    onNavigateBack: () -> Unit,
    isEditingMode: Boolean = false,
    ideaId: Int = 0,
    onToggleTheme: (() -> Unit)? = null,
    isDarkTheme: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBarWithBack(
                title = if (isEditingMode) "Editar Idea" else "Nueva Idea",
                onBackClick = onNavigateBack,
                onToggleTheme = onToggleTheme,
                isDarkTheme = isDarkTheme
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: Título
            RoundedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                placeholder = "Título de tu idea..."
            )

            // Campo: Descripción
            RoundedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                placeholder = "Describe brevemente tu idea...",
                minLines = 4,
                maxLines = 6
            )

            // Dropdown: Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                RoundedTextField(
                    value = if (uiState.categoria.isNotEmpty())
                        Categoria.fromString(uiState.categoria).displayName
                    else "",
                    onValueChange = {},
                    placeholder = "Seleccionar categoría",
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    Categoria.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.displayName) },
                            onClick = {
                                viewModel.onCategoriaChange(categoria.name)
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Dropdown: Prioridad
            ExposedDropdownMenuBox(
                expanded = expandedPrioridad,
                onExpandedChange = { expandedPrioridad = !expandedPrioridad }
            ) {
                RoundedTextField(
                    value = if (uiState.prioridad.isNotEmpty())
                        Prioridad.fromString(uiState.prioridad).displayName
                    else "",
                    onValueChange = {},
                    placeholder = "Seleccionar prioridad",
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedPrioridad,
                    onDismissRequest = { expandedPrioridad = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(prioridad.displayName) },
                            onClick = {
                                viewModel.onPrioridadChange(prioridad.name)
                                expandedPrioridad = false
                            }
                        )
                    }
                }
            }

            // Dropdown: Estado
            ExposedDropdownMenuBox(
                expanded = expandedEstado,
                onExpandedChange = { expandedEstado = !expandedEstado }
            ) {
                RoundedTextField(
                    value = if (uiState.estado.isNotEmpty())
                        Estado.fromString(uiState.estado).displayName
                    else "",
                    onValueChange = {},
                    placeholder = "Seleccionar estado",
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedEstado,
                    onDismissRequest = { expandedEstado = false }
                ) {
                    Estado.entries.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.displayName) },
                            onClick = {
                                viewModel.onEstadoChange(estado.name)
                                expandedEstado = false
                            }
                        )
                    }
                }
            }

            // Campo: Recursos necesarios
            RoundedTextField(
                value = uiState.recursosNecesarios,
                onValueChange = { viewModel.onRecursosChange(it) },
                placeholder = "Recursos necesarios (ej: laptop, tiempo, equipo...)",
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón principal
            Button(
                onClick = {
                    if (isEditingMode) {
                        viewModel.updateExistingIdea(ideaId)
                    } else {
                        viewModel.onGuardarClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = if (isEditingMode) Icons.Default.Edit else Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditingMode) "Actualizar Idea" else "Guardar Idea")
                }
            }

            // Efecto cuando se guarda exitosamente
            LaunchedEffect(uiState.guardadoExitoso) {
                if (uiState.guardadoExitoso) {
                    delay(500)
                    viewModel.resetForm()
                    onNavigateBack()
                }
            }
        }
    }
}

// 🪶 Campo de texto con fondo redondeado y placeholder minimalista
@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    // 🔹 CAMBIO: usamos un color suave del tema para el fondo del campo
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // 🔹 Muestra el placeholder solo cuando el campo está vacío
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
        }

        // 🔹 CAMBIO: usamos BasicTextField (no TextField) → sin líneas inferiores ni borde
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            readOnly = readOnly,
            minLines = minLines,
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),

            // 🆕 NUEVO: definimos color del cursor (sin afectar líneas inferiores)
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
}
