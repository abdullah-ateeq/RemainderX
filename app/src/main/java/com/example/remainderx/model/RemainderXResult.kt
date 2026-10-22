package com.example.remainderx.model

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Result model representing an authoritative integer division calculation.
 */
data class RemainderXResult(
    val dividend: BigInteger,
    val divisor: BigInteger,
    val quotient: BigInteger,
    val remainder: BigInteger,
    val absoluteRemainder: BigInteger,
    val decimalResult: BigDecimal,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)
