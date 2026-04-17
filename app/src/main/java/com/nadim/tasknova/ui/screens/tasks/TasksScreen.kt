package com.nadim.tasknova.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.data.model.Task
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks          by viewModel.tasks.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val filters = listOf("all", "pending", "done", "missed")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "My Tasks",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Filter chips
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick  = { viewModel.setFilter(filter) },
                        label    = { Text(filter.replaceFirstChar { it.uppercase() }) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AriaGreen,
                            selectedLabelColor     = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks list
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", fontSize = androidx.compose.ui.unit.TextUnit(48f,
                            androidx.compose.ui.unit.TextUnitType.Sp), color = TextHint)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "No tasks here",
                            color = TextHint,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text  = "Talk to Aria to create one",
                            color = TextHint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding        = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement   = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks) { task ->
                        TaskCard(
                            task     = task,
                            onDone   = { viewModel.markDone(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDone: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AriaCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        when (task.priority) {
                            "high" -> PriorityHigh
                            "low"  -> PriorityLow
                            else   -> PriorityMed
                        }
                    )
            )
            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = task.title,
                    color      = if (task.status == "done") TextHint else TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style      = MaterialTheme.typography.bodyLarge
                )
                task.description?.let {
                    Text(
                        text  = it,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Status badge
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color  = when (task.status) {
                        "done"    -> StatusDone.copy(alpha = 0.15f)
                        "missed"  -> StatusMissed.copy(alpha = 0.15f)
                        else      -> StatusPending.copy(alpha = 0.15f)
                    },
                    shape  = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text     = task.status.replaceFirstChar { it.uppercase() },
                        color    = when (task.status) {
                            "done"   -> StatusDone
                            "missed" -> StatusMissed
                            else     -> StatusPending
                        },
                        style    = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (task.status == "pending") {
                IconButton(onClick = onDone) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = AriaGreen)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextHint)
            }
        }
    }
}