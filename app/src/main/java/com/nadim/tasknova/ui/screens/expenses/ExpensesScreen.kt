package com.nadim.tasknova.ui.screens.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.data.model.Expense
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.ExpenseViewModel

@Composable
fun ExpensesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses         by viewModel.expenses.collectAsState()
    val total            by viewModel.totalAmount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf("all", "food", "travel", "work", "health", "other")

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
                    text       = "Expenses",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Total card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AriaCard)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total spent", color = TextSecondary)
                    Text(
                        text       = "€${"%.2f".format(total)}",
                        color      = AriaGreen,
                        fontSize   = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding        = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick  = { viewModel.setCategory(cat) },
                        label    = { Text(cat.replaceFirstChar { it.uppercase() }) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AriaGreen,
                            selectedLabelColor     = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No expenses logged.\nAsk Aria to log one!",
                        color     = TextHint,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseCard(
                            expense  = expense,
                            onDelete = { viewModel.deleteExpense(expense) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, onDelete: () -> Unit) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AriaCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = when (expense.category) {
                    "food"   -> "🍔"
                    "travel" -> "✈️"
                    "work"   -> "💼"
                    "health" -> "🏥"
                    else     -> "💳"
                },
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = expense.description ?: expense.category ?: "Expense",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = expense.category?.replaceFirstChar { it.uppercase() } ?: "",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text       = "€${"%.2f".format(expense.amount)}",
                color      = AriaGreen,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextHint)
            }
        }
    }
}