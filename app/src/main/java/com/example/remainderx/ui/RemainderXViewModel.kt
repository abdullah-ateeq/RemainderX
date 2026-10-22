package com.example.remainderx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remainderx.data.repository.HistoryRepository
import com.example.remainderx.data.repository.SettingsRepository
import com.example.remainderx.domain.RemainderXParser
import com.example.remainderx.domain.RemainderXValidator
import com.example.remainderx.domain.ValidationResult
import com.example.remainderx.domain.math.RemainderXCalculator
import com.example.remainderx.model.AppSettings
import com.example.remainderx.model.RemainderXHistory
import com.example.remainderx.model.RemainderXResult
import com.example.remainderx.model.RemainderXUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.MathContext
import java.math.RoundingMode

class RemainderXViewModel(
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemainderXUiState())
    val uiState: StateFlow<RemainderXUiState> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow(settingsRepository.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val (lastDividend, lastDivisor) = settingsRepository.getLastInputs()
        val historyList = historyRepository.getHistory()

        _uiState.update {
            it.copy(
                dividendInput = lastDividend,
                divisorInput = lastDivisor,
                history = historyList
            )
        }

        // If inputs were already present from last session, calculate automatically
        if (lastDividend.isNotBlank() && lastDivisor.isNotBlank()) {
            calculate()
        }
    }

    fun onDividendChanged(newDividend: String) {
        _uiState.update {
            it.copy(
                dividendInput = newDividend,
                errorMessage = null
            )
        }
    }

    fun onDivisorChanged(newDivisor: String) {
        _uiState.update {
            it.copy(
                divisorInput = newDivisor,
                errorMessage = null
            )
        }
    }

    /**
     * Swaps Dividend and Divisor inputs.
     * Result is cleared until user presses Calculate.
     */
    fun swapInputs() {
        _uiState.update {
            val oldDividend = it.dividendInput
            val oldDivisor = it.divisorInput
            it.copy(
                dividendInput = oldDivisor,
                divisorInput = oldDividend,
                result = null,
                errorMessage = null
            )
        }
    }

    /**
     * Clears inputs and current result. Preserves history.
     */
    fun clearInputs() {
        _uiState.update {
            it.copy(
                dividendInput = "",
                divisorInput = "",
                result = null,
                errorMessage = null
            )
        }
        settingsRepository.saveLastInputs("", "")
    }

    /**
     * Executes authoritative BigInteger division calculation.
     */
    fun calculate() {
        val currentState = _uiState.value
        val validation = RemainderXValidator.validateInputs(
            currentState.dividendInput,
            currentState.divisorInput
        )

        when (validation) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = validation.message,
                        result = null,
                        isCalculating = false
                    )
                }
            }

            is ValidationResult.Success -> {
                val dividend = validation.dividend
                val divisor = validation.divisor

                val quotient = RemainderXCalculator.calculateQuotient(dividend, divisor)
                val remainder = RemainderXCalculator.calculateRemainder(dividend, divisor)
                val absoluteRemainder = RemainderXCalculator.calculateAbsoluteRemainder(remainder)

                // High precision 50-digit decimal context for internal precision retention
                val mathContext = MathContext(50, RoundingMode.HALF_UP)
                val decimalResult = RemainderXCalculator.calculateDecimalResult(
                    dividend,
                    divisor,
                    mathContext
                )

                val isVerified = RemainderXCalculator.verifyCalculation(
                    dividend,
                    divisor,
                    quotient,
                    remainder
                )

                if (isVerified) {
                    val steps = RemainderXCalculator.generateCalculationSteps(
                        dividend,
                        divisor,
                        quotient,
                        remainder
                    )

                    val result = RemainderXResult(
                        dividend = dividend,
                        divisor = divisor,
                        quotient = quotient,
                        remainder = remainder,
                        absoluteRemainder = absoluteRemainder,
                        decimalResult = decimalResult,
                        calculationSteps = steps,
                        calculatedAt = System.currentTimeMillis()
                    )

                    _uiState.update {
                        it.copy(
                            result = result,
                            errorMessage = null,
                            isCalculating = false
                        )
                    }

                    settingsRepository.saveLastInputs(
                        currentState.dividendInput.trim(),
                        currentState.divisorInput.trim()
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Calculation verification failed internally.",
                            result = null,
                            isCalculating = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Explicitly saves current calculation to history.
     */
    fun saveCurrentCalculation(): Boolean {
        val result = _uiState.value.result ?: return false
        val saved = historyRepository.saveCalculation(
            result.dividend.toString(),
            result.divisor.toString()
        )
        if (saved) {
            _uiState.update {
                it.copy(history = historyRepository.getHistory())
            }
        }
        return saved
    }

    /**
     * Restores inputs from a history entry and recalculates immediately.
     */
    fun restoreHistoryItem(item: RemainderXHistory) {
        _uiState.update {
            it.copy(
                dividendInput = item.dividend,
                divisorInput = item.divisor,
                errorMessage = null
            )
        }
        calculate()
    }

    /**
     * Deletes a specific history item.
     */
    fun deleteHistoryItem(id: String) {
        historyRepository.deleteHistoryItem(id)
        _uiState.update {
            it.copy(history = historyRepository.getHistory())
        }
    }

    /**
     * Clears all history entries.
     */
    fun clearAllHistory() {
        historyRepository.clearHistory()
        _uiState.update {
            it.copy(history = emptyList())
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        settingsRepository.updateSettings(newSettings)
        _settings.value = newSettings
    }

    fun resetSettings() {
        val defaults = settingsRepository.resetSettings()
        _settings.value = defaults
    }

    class Factory(
        private val historyRepository: HistoryRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RemainderXViewModel::class.java)) {
                return RemainderXViewModel(historyRepository, settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
