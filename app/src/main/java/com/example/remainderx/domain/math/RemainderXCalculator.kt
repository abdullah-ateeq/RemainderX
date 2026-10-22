package com.example.remainderx.domain.math

import com.example.remainderx.model.CalculationStep
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

/**
 * Domain math engine executing all authoritative calculations using BigInteger.
 * Never uses primitive Double, Float, Long, or Int for core arithmetic.
 */
object RemainderXCalculator {

    /**
     * Calculates quotient using BigInteger integer division (truncates toward zero).
     */
    fun calculateQuotient(dividend: BigInteger, divisor: BigInteger): BigInteger {
        require(divisor != BigInteger.ZERO) { "A number cannot be divided by zero." }
        return dividend.divide(divisor)
    }

    /**
     * Calculates remainder using BigInteger remainder (shares sign of dividend).
     */
    fun calculateRemainder(dividend: BigInteger, divisor: BigInteger): BigInteger {
        require(divisor != BigInteger.ZERO) { "A number cannot be divided by zero." }
        return dividend.remainder(divisor)
    }

    /**
     * Calculates absolute remainder: |Remainder|.
     */
    fun calculateAbsoluteRemainder(remainder: BigInteger): BigInteger {
        return remainder.abs()
    }

    /**
     * Calculates high-precision decimal representation using BigDecimal.
     */
    fun calculateDecimalResult(
        dividend: BigInteger,
        divisor: BigInteger,
        mathContext: MathContext
    ): BigDecimal {
        require(divisor != BigInteger.ZERO) { "A number cannot be divided by zero." }
        if (dividend == BigInteger.ZERO) {
            return BigDecimal.ZERO
        }
        return dividend.toBigDecimal().divide(divisor.toBigDecimal(), mathContext)
    }

    /**
     * Calculates decimal representation formatted with the requested scale and rounding mode.
     */
    fun calculateDecimalResult(
        dividend: BigInteger,
        divisor: BigInteger,
        scale: Int,
        roundingMode: RoundingMode
    ): BigDecimal {
        require(divisor != BigInteger.ZERO) { "A number cannot be divided by zero." }
        if (dividend == BigInteger.ZERO) {
            return BigDecimal.ZERO.setScale(scale, roundingMode)
        }
        return dividend.toBigDecimal().divide(divisor.toBigDecimal(), scale, roundingMode)
    }

    /**
     * Verifies that: Divisor × Quotient + Remainder == Dividend
     * and |Remainder| < |Divisor|.
     */
    fun verifyCalculation(
        dividend: BigInteger,
        divisor: BigInteger,
        quotient: BigInteger,
        remainder: BigInteger
    ): Boolean {
        if (divisor == BigInteger.ZERO) return false
        val product = divisor.multiply(quotient)
        val reconstructed = product.add(remainder)
        val mathMatch = reconstructed == dividend
        val rangeValid = remainder.abs() < divisor.abs()
        return mathMatch && rangeValid
    }

    /**
     * Checks if division is exact (remainder == 0).
     */
    fun isExactDivision(remainder: BigInteger): Boolean {
        return remainder == BigInteger.ZERO
    }

    /**
     * Generates structured step-by-step breakdown of the integer division.
     */
    fun generateCalculationSteps(
        dividend: BigInteger,
        divisor: BigInteger,
        quotient: BigInteger,
        remainder: BigInteger
    ): List<CalculationStep> {
        val product = divisor.multiply(quotient)
        val steps = mutableListOf<CalculationStep>()

        steps.add(
            CalculationStep(
                label = "Multiplication",
                expression = "$divisor × $quotient",
                result = product.toString()
            )
        )

        steps.add(
            CalculationStep(
                label = "Subtraction",
                expression = "$dividend − $product",
                result = remainder.toString()
            )
        )

        steps.add(
            CalculationStep(
                label = "Quotient & Remainder",
                expression = "Quotient = $quotient, Remainder = $remainder",
                result = if (remainder == BigInteger.ZERO) "Divides exactly" else "Has a remainder"
            )
        )

        steps.add(
            CalculationStep(
                label = "Verification",
                expression = "$divisor × $quotient + $remainder",
                result = "= $dividend"
            )
        )

        return steps
    }
}
