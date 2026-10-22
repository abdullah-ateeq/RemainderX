package com.example.remainderx.model

/**
 * Represents a single step in the division calculation.
 */
data class CalculationStep(
    val label: String,
    val expression: String,
    val result: String
)
