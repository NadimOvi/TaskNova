package com.nadim.tasknova.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.data.model.Reminder
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.ReminderViewModel

@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val reminders      by viewModel.reminders.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filters = listOf("all", "pending", "done", "missed")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text       = "Reminders",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

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

            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reminders yet.\nAsk Aria to set one!", color = TextHint,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onDone   = { viewModel.markDone(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onDone: () -> Unit, onDelete: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = reminder.title,
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                reminder.recurrence?.let {
                    Text(text = "Repeats: $it", color = AriaGreen, style = MaterialTheme.typography.bodyMedium)
                }
                Surface(
                    color = when (reminder.status) {
                        "done"   -> StatusDone.copy(alpha = 0.15f)
                        "missed" -> StatusMissed.copy(alpha = 0.15f)
                        else     -> StatusPending.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text  = reminder.status.replaceFirstChar { it.uppercase() },
                        color = when (reminder.status) {
                            "done"   -> StatusDone
                            "missed" -> StatusMissed
                            else     -> StatusPending
                        },
                        style    = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            if (reminder.status == "pending") {
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