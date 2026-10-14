package com.example.lab08

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TodoistRed = Color(0xFFDB4C3F)
private val TodoistLightRed = Color(0xFFFCE8E6)
private val TodoistGray = Color(0xFF808080)
private val LineGray = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val filter by viewModel.filter.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TodoistRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bandeja de entrada",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { viewModel.deleteAllTasks() }) {
                    Text("Borrar todas", color = TodoistRed)
                }
            }
            Text(
                text = "${tasks.size} tareas",
                color = TodoistGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            FilterRow(current = filter, onSelect = viewModel::setFilter)

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tareas", color = TodoistGray)
                }
            } else {
                LazyColumn {
                    items(tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { taskToEdit = task },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TaskDialog(
            title = "Nueva tarea",
            initialText = "",
            confirmLabel = "Agregar",
            onDismiss = { showAddDialog = false },
            onConfirm = { viewModel.addTask(it) }
        )
    }

    taskToEdit?.let { task ->
        TaskDialog(
            title = "Editar tarea",
            initialText = task.description,
            confirmLabel = "Guardar",
            onDismiss = { taskToEdit = null },
            onConfirm = { viewModel.editTask(task, it) }
        )
    }
}

@Composable
fun FilterRow(current: TaskFilter, onSelect: (TaskFilter) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.entries.forEach { f ->
            FilterChip(
                selected = current == f,
                onClick = { onSelect(f) },
                label = { Text(f.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TodoistLightRed,
                    selectedLabelColor = TodoistRed
                )
            )
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) TodoistGray else Color.Transparent)
                    .border(2.dp, if (task.isCompleted) TodoistGray else TodoistRed, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completada",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = task.description,
                fontSize = 16.sp,
                color = if (task.isCompleted) TodoistGray else Color.Black,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                else TextDecoration.None,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .clickable { onEdit() }
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = TodoistGray
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 52.dp)
                .height(1.dp)
                .background(LineGray)
        )
    }
}

@Composable
fun TaskDialog(
    title: String,
    initialText: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Descripción") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (text.isNotBlank()) {
                    onConfirm(text.trim())
                    onDismiss()
                }
            }) { Text(confirmLabel, color = TodoistRed) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TodoistGray) }
        }
    )
}