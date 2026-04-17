package com.nadim.tasknova.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.data.model.Task
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.ProfileViewModel
import com.nadim.tasknova.viewmodel.TaskViewModel
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToVoice: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profile.collectAsState()
    val tasks   by taskViewModel.tasks.collectAsState()
    val pendingTasks = tasks.filter { it.status == "pending" }.take(3)

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11  -> "Good morning"
        in 12..17 -> "Good afternoon"
        else      -> "Good evening"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // ── Header ──────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = "$greeting,",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text       = profile?.fullName
                                ?.split(" ")
                                ?.firstOrNull() ?: "there",
                            color      = TextPrimary,
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AriaSurface)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = profile?.fullName
                                ?.firstOrNull()?.toString() ?: "A",
                            color      = AriaGreen,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Quick actions ────────────────────────────
            item {
                Text(
                    text     = "Quick actions",
                    color    = TextSecondary,
                    style    = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val quickItems = listOf(
                        Triple("Tasks",     Icons.Default.CheckCircle,    onNavigateToTasks),
                        Triple("Reminders", Icons.Default.Alarm,          onNavigateToReminders),
                        Triple("Notes",     Icons.Default.Note,           onNavigateToNotes),
                        Triple("Expenses",  Icons.Default.AccountBalance, onNavigateToExpenses)
                    )
                    items(quickItems) { (label, icon, action) ->
                        QuickActionCard(
                            label   = label,
                            icon    = icon,
                            onClick = action
                        )
                    }
                }
            }

            // ── Aria banner ──────────────────────────────
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { onNavigateToVoice() },
                    shape  = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AriaCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = "✦ Aria",
                                color      = AriaGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text  = "Tap the mic and tell me what you need.",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint     = AriaGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // ── Pending tasks ────────────────────────────
            if (pendingTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = "Pending tasks",
                            color      = TextPrimary,
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToTasks) {
                            Text("See all", color = AriaGreen)
                        }
                    }
                }
                items(pendingTasks) { task ->
                    TaskItemCard(
                        task   = task,
                        onDone = { taskViewModel.markDone(task) }
                    )
                }
            } else {
                // Empty state
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text  = "✓",
                            color = TextHint,
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "All clear!",
                            color = TextHint,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text  = "Talk to Aria to create a task",
                            color = TextHint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // ── Voice FAB ────────────────────────────────────
        FloatingActionButton(
            onClick        = onNavigateToVoice,
            modifier       = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp),
            containerColor = AriaGreen,
            contentColor   = Color.Black,
            shape          = CircleShape
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = "Talk to Aria",
                modifier           = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(90.dp)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AriaCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint     = AriaGreen,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text       = label,
                color      = TextPrimary,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TaskItemCard(task: Task, onDone: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AriaCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (task.priority) {
                            "high" -> PriorityHigh
                            "low"  -> PriorityLow
                            else   -> PriorityMed
                        }
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = task.title,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    style      = MaterialTheme.typography.bodyLarge
                )
                task.description?.let {
                    Text(
                        text  = it,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            IconButton(onClick = onDone) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Mark done",
                    tint     = AriaGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}