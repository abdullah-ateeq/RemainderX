package com.example.remainderx.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remainderx.model.AppRoundingMode
import com.example.remainderx.model.AppTheme
import com.example.remainderx.ui.RemainderXViewModel
import com.example.remainderx.util.HapticHelper

@Composable
fun SettingsScreen(
    viewModel: RemainderXViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    var themeMenuExpanded by remember { mutableStateOf(false) }
    var decimalMenuExpanded by remember { mutableStateOf(false) }
    var roundingMenuExpanded by remember { mutableStateOf(false) }

    val decimalOptions = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Preferences and display configurations",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // APPEARANCE SECTION
        SettingsSectionTitle(title = "APPEARANCE")

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Theme
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticHelper.performClick(view, settings.hapticFeedback)
                                themeMenuExpanded = true
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Theme", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text(
                                text = when (settings.theme) {
                                    AppTheme.SYSTEM -> "System default"
                                    AppTheme.LIGHT -> "Light"
                                    AppTheme.DARK -> "Dark"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = themeMenuExpanded,
                        onDismissRequest = { themeMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("System") },
                            onClick = {
                                viewModel.updateSettings(settings.copy(theme = AppTheme.SYSTEM))
                                themeMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Light") },
                            onClick = {
                                viewModel.updateSettings(settings.copy(theme = AppTheme.LIGHT))
                                themeMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Dark") },
                            onClick = {
                                viewModel.updateSettings(settings.copy(theme = AppTheme.DARK))
                                themeMenuExpanded = false
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Dynamic Color
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Dynamic Color", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            text = "Apply system wallpaper accent colors (Android 12+)",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = settings.dynamicColor,
                        onCheckedChange = {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(dynamicColor = it))
                        }
                    )
                }
            }
        }

        // BEHAVIOR SECTION
        SettingsSectionTitle(title = "BEHAVIOR")

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Haptics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Haptic Feedback", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            text = "Vibrate on button clicks and input actions",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(hapticFeedback = it))
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Animations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Animations", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            text = "Smooth transitions for results and cards",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = settings.animations,
                        onCheckedChange = {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(animations = it))
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Confirm Delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Confirm Delete", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            text = "Show confirmation dialog before removing history",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = settings.confirmDelete,
                        onCheckedChange = {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(confirmDelete = it))
                        }
                    )
                }
            }
        }

        // DECIMAL DISPLAY SECTION
        SettingsSectionTitle(title = "DECIMAL DISPLAY")

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Decimal Places
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticHelper.performClick(view, settings.hapticFeedback)
                                decimalMenuExpanded = true
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Decimal Places", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text(
                                text = "${settings.decimalPlaces} decimal places",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = decimalMenuExpanded,
                        onDismissRequest = { decimalMenuExpanded = false }
                    ) {
                        for (opt in decimalOptions) {
                            DropdownMenuItem(
                                text = { Text("$opt places") },
                                onClick = {
                                    viewModel.updateSettings(settings.copy(decimalPlaces = opt))
                                    decimalMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Rounding Mode
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticHelper.performClick(view, settings.hapticFeedback)
                                roundingMenuExpanded = true
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Rounding Mode", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text(
                                text = settings.roundingMode.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = roundingMenuExpanded,
                        onDismissRequest = { roundingMenuExpanded = false }
                    ) {
                        for (mode in AppRoundingMode.entries) {
                            DropdownMenuItem(
                                text = { Text(mode.displayName) },
                                onClick = {
                                    viewModel.updateSettings(settings.copy(roundingMode = mode))
                                    roundingMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // DATA MANAGEMENT
        SettingsSectionTitle(title = "DATA")

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        showClearHistoryDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Clear History", color = MaterialTheme.colorScheme.error)
                }

                OutlinedButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        showResetDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Reset Settings")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset all settings?") },
            text = {
                Text(text = "This resets theme, dynamic color, haptics, animations, decimal places, and rounding mode to default. Saved calculation history will be preserved.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        viewModel.resetSettings()
                        showResetDialog = false
                        Toast.makeText(context, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Reset", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    // Clear history confirmation dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(text = "Clear all saved calculations?") },
            text = {
                Text(text = "This will permanently remove all saved calculation history.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticHelper.performClick(view, settings.hapticFeedback)
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}
