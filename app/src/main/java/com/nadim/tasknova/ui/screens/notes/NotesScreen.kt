package com.nadim.tasknova.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.data.model.Note
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.NoteViewModel

@Composable
fun NotesScreen(
    onNavigateBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Notes",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No notes yet.\nAsk Aria to take one!",
                        color     = TextHint,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(note = note, onDelete = { viewModel.deleteNote(note) })
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: Note, onDelete: () -> Unit) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AriaCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                note.title?.let {
                    Text(text = it, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Text(text = note.content, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                note.summary?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Summary: $it", color = AriaGreen, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextHint)
            }
        }
    }
}