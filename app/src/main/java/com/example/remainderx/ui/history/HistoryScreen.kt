package com.example.remainderx.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remainderx.domain.RemainderXParser
import com.example.remainderx.domain.math.RemainderXCalculator
import com.example.remainderx.model.RemainderXHistory
import com.example.remainderx.ui.RemainderXViewModel
import com.example.remainderx.util.HapticHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: RemainderXViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val view = LocalView.current

    var itemToDelete by remember { mutableStateOf<RemainderXHistory?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Saved division calculations (${uiState.history.size}/10)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (uiState.history.isNotEmpty()) {
                FilledTonalButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        showClearAllDialog = true
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved calculations yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap 'Save' on any calculation to preserve it here.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.history,
                    key = { it.id }
                ) { item ->
                    // Calculate preview quotient and remainder dynamically
                    val previewQR = remember(item.dividend, item.divisor) {
                        try {
                            val d1 = RemainderXParser.parseInteger(item.dividend).getOrNull()
                            val d2 = RemainderXParser.parseInteger(item.divisor).getOrNull()
                            if (d1 != null && d2 != null && d2 != java.math.BigInteger.ZERO) {
                                val q = RemainderXCalculator.calculateQuotient(d1, d2)
                                val r = RemainderXCalculator.calculateRemainder(d1, d2)
                                "Q $q • R $r"
                            } else {
                                ""
                            }
                        } catch (e: Exception) {
                            ""
                        }
                    }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissVal ->
                            if (dismissVal == SwipeToDismissBoxValue.EndToStart || dismissVal == SwipeToDismissBoxValue.StartToEnd) {
                                if (settings.confirmDelete) {
                                    itemToDelete = item
                                    false
                                } else {
                                    HapticHelper.performClick(view, settings.hapticFeedback)
                                    viewModel.deleteHistoryItem(item.id)
                                    true
                                }
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticHelper.performClick(view, settings.hapticFeedback)
                                    viewModel.restoreHistoryItem(item)
                                    onNavigateToHome()
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.dividend} ÷ ${item.divisor}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    if (previewQR.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = previewQR,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatHistoryTimestamp(item.savedAt),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (settings.confirmDelete) {
                                                itemToDelete = item
                                            } else {
                                                HapticHelper.performClick(view, settings.hapticFeedback)
                                                viewModel.deleteHistoryItem(item.id)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete calculation",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Single item delete confirmation dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(text = "Delete calculation?") },
            text = {
                Text(text = "Are you sure you want to delete ${itemToDelete?.dividend} ÷ ${itemToDelete?.divisor}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            viewModel.deleteHistoryItem(it.id)
                        }
                        itemToDelete = null
                    }
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    // Clear all history confirmation dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text(text = "Clear all saved calculations?") },
            text = {
                Text(text = "This will permanently remove all 10 saved calculation entries.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        viewModel.clearAllHistory()
                        showClearAllDialog = false
                    }
                ) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

private fun formatHistoryTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val sameDayFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())

    val todayCalendar = java.util.Calendar.getInstance()
    val itemCalendar = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (todayCalendar.get(java.util.Calendar.YEAR) == itemCalendar.get(java.util.Calendar.YEAR) &&
        todayCalendar.get(java.util.Calendar.DAY_OF_YEAR) == itemCalendar.get(java.util.Calendar.DAY_OF_YEAR)
    ) {
        "Today • ${sameDayFormat.format(Date(timestamp))}"
    } else if (todayCalendar.get(java.util.Calendar.YEAR) == itemCalendar.get(java.util.Calendar.YEAR) &&
        todayCalendar.get(java.util.Calendar.DAY_OF_YEAR) - itemCalendar.get(java.util.Calendar.DAY_OF_YEAR) == 1
    ) {
        "Yesterday • ${sameDayFormat.format(Date(timestamp))}"
    } else {
        fullDateFormat.format(Date(timestamp))
    }
}
