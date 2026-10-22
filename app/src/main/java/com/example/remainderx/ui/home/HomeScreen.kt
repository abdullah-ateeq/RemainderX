package com.example.remainderx.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remainderx.domain.RemainderXParser
import com.example.remainderx.ui.RemainderXViewModel
import com.example.remainderx.ui.home.components.ResultSection
import com.example.remainderx.util.HapticHelper

@Composable
fun HomeScreen(
    viewModel: RemainderXViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var swapRotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = swapRotation,
        animationSpec = tween(if (settings.animations) 300 else 0),
        label = "swapRotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header & Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "RemainderX",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calculate division quotient and remainder.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card Container
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // DIVIDEND INPUT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIVIDEND",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${uiState.dividendInput.length}/${RemainderXParser.MAX_INPUT_LENGTH}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (uiState.dividendInput.length > RemainderXParser.MAX_INPUT_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        )
                    }

                    Text(
                        text = "The number being divided.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.dividendInput,
                        onValueChange = { viewModel.onDividendChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Number being divided."
                            },
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("e.g. 17") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.dividendInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            HapticHelper.performClick(view, settings.hapticFeedback)
                                            viewModel.onDividendChanged("")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear dividend input",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        HapticHelper.performClick(view, settings.hapticFeedback)
                                        val current = uiState.dividendInput
                                        val toggled = if (current.startsWith("-")) current.substring(1) else "-$current"
                                        viewModel.onDividendChanged(toggled)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Exposure,
                                        contentDescription = "Toggle sign",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SWAP BUTTON
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            swapRotation += 180f
                            viewModel.swapInputs()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .semantics {
                                contentDescription = "Swap dividend and divisor."
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap inputs",
                            modifier = Modifier.rotate(animatedRotation)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DIVISOR INPUT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIVISOR",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${uiState.divisorInput.length}/${RemainderXParser.MAX_INPUT_LENGTH}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (uiState.divisorInput.length > RemainderXParser.MAX_INPUT_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        )
                    }

                    Text(
                        text = "The number you are dividing by.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.divisorInput,
                        onValueChange = { viewModel.onDivisorChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Number dividing the dividend."
                            },
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("e.g. 5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.calculate()
                            }
                        ),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.divisorInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            HapticHelper.performClick(view, settings.hapticFeedback)
                                            viewModel.onDivisorChanged("")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear divisor input",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        HapticHelper.performClick(view, settings.hapticFeedback)
                                        val current = uiState.divisorInput
                                        val toggled = if (current.startsWith("-")) current.substring(1) else "-$current"
                                        viewModel.onDivisorChanged(toggled)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Exposure,
                                        contentDescription = "Toggle sign",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CALCULATE & CLEAR BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.performClick(view, settings.hapticFeedback)
                            viewModel.clearInputs()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "CLEAR")
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val validation = com.example.remainderx.domain.RemainderXValidator.validateInputs(
                                uiState.dividendInput,
                                uiState.divisorInput
                            )
                            if (validation is com.example.remainderx.domain.ValidationResult.Error) {
                                HapticHelper.performError(view, settings.hapticFeedback)
                            } else {
                                HapticHelper.performClick(view, settings.hapticFeedback)
                            }
                            viewModel.calculate()
                        },
                        modifier = Modifier
                            .weight(2f)
                            .semantics {
                                contentDescription = "Calculate quotient and remainder."
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "CALCULATE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }

        // ERROR MESSAGE DISPLAY
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(animationSpec = tween(if (settings.animations) 200 else 0)),
            exit = fadeOut(animationSpec = tween(if (settings.animations) 200 else 0))
        ) {
            uiState.errorMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // RESULT SECTION
        AnimatedVisibility(
            visible = uiState.result != null,
            enter = fadeIn(animationSpec = tween(if (settings.animations) 250 else 0)) +
                    slideInVertically(
                        initialOffsetY = { if (settings.animations) it / 6 else 0 },
                        animationSpec = tween(if (settings.animations) 250 else 0)
                    ),
            exit = fadeOut(animationSpec = tween(if (settings.animations) 200 else 0))
        ) {
            uiState.result?.let { res ->
                ResultSection(
                    result = res,
                    settings = settings,
                    onSaveRequested = {
                        val saved = viewModel.saveCurrentCalculation()
                        if (saved) {
                            Toast.makeText(context, "Calculation saved to history", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Already saved to history", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
