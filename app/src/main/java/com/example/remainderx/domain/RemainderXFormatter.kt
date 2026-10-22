package com.example.remainderx.domain

import com.example.remainderx.model.RemainderXResult
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

object RemainderXFormatter {

    /**
     * Formats the authoritative verification equation:
     * Divisor × Quotient + Remainder = Dividend
     */
    fun formatVerification(
        dividend: BigInteger,
        divisor: BigInteger,
        quotient: BigInteger,
        remainder: BigInteger
    ): String {
        return "$divisor × $quotient + $remainder = $dividend"
    }

    /**
     * Formats the mixed number representation.
     * E.g. 17 ÷ 5 -> "3 2/5"
     * 20 ÷ 5 -> "4" (no unnecessary fraction when remainder is zero)
     * 2 ÷ 5 -> "2/5"
     * -17 ÷ 5 -> "-3 2/5"
     */
    fun formatMixedNumber(
        quotient: BigInteger,
        remainder: BigInteger,
        divisor: BigInteger
    ): String {
        if (remainder == BigInteger.ZERO) {
            return quotient.toString()
        }

        val absRemainder = remainder.abs()
        val absDivisor = divisor.abs()

        return if (quotient == BigInteger.ZERO) {
            // Determine sign of fraction
            val isNegative = (remainder < BigInteger.ZERO) xor (divisor < BigInteger.ZERO)
            if (isNegative) "-$absRemainder/$absDivisor" else "$absRemainder/$absDivisor"
        } else {
            "$quotient $absRemainder/$absDivisor"
        }
    }

    /**
     * Formats decimal representation according to precision settings.
     * Strips redundant trailing zeros unless scale is 0.
     */
    fun formatDecimal(
        decimalResult: BigDecimal,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        if (decimalPlaces == 0) {
            return decimalResult.setScale(0, roundingMode).toPlainString()
        }
        if (decimalResult.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val scaled = decimalResult.setScale(decimalPlaces, roundingMode)
        val plain = scaled.stripTrailingZeros().toPlainString()
        return if (plain == "-0") "0" else plain
    }

    /**
     * Generates a clean text tree breakdown for visual card.
     */
    fun formatVisualTree(
        dividend: BigInteger,
        divisor: BigInteger,
        quotient: BigInteger,
        remainder: BigInteger
    ): String {
        val qAbs = quotient.abs()
        val maxTreeGroups = 10
        if (qAbs <= BigInteger.valueOf(maxTreeGroups.toLong()) && qAbs > BigInteger.ZERO) {
            val count = qAbs.toInt()
            val sb = StringBuilder()
            sb.append("$dividend\n")
            val groupVal = divisor.abs()
            for (i in 0 until count) {
                if (i == count - 1 && remainder == BigInteger.ZERO) {
                    sb.append("└── $groupVal (exact)")
                } else {
                    sb.append("├── $groupVal\n")
                }
            }
            if (remainder != BigInteger.ZERO) {
                sb.append("└── ${remainder.abs()} remaining")
            }
            return sb.toString()
        } else {
            val groupText = if (quotient == BigInteger.ONE || quotient == BigInteger.valueOf(-1)) "group" else "groups"
            val exactText = if (remainder == BigInteger.ZERO) "0 left over (divides exactly)" else "${remainder.abs()} remaining"
            return "$dividend ÷ $divisor\n\n${quotient.abs()} complete $groupText\n$exactText"
        }
    }

    /**
     * Formats standard copy output according to prompt specification.
     */
    fun formatCopyResult(
        result: RemainderXResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val decimalStr = formatDecimal(result.decimalResult, decimalPlaces, roundingMode)
        val verificationStr = formatVerification(
            result.dividend,
            result.divisor,
            result.quotient,
            result.remainder
        )

        return """
            RemainderX

            Dividend: ${result.dividend}
            Divisor: ${result.divisor}

            Quotient: ${result.quotient}
            Remainder: ${result.remainder}
            Absolute Remainder: ${result.absoluteRemainder}
            Decimal Result: $decimalStr

            Verification:
            $verificationStr
        """.trimIndent()
    }

    /**
     * Formats detailed step calculation copy according to prompt specification.
     */
    fun formatCopyCalculation(result: RemainderXResult): String {
        val product = result.divisor.multiply(result.quotient)
        val verificationStr = formatVerification(
            result.dividend,
            result.divisor,
            result.quotient,
            result.remainder
        )

        return """
            ${result.dividend} ÷ ${result.divisor}

            ${result.divisor} × ${result.quotient} = $product
            ${result.dividend} − $product = ${result.remainder}

            Quotient = ${result.quotient}
            Remainder = ${result.remainder}

            Verification:
            $verificationStr
        """.trimIndent()
    }

    /**
     * Formats share message.
     */
    fun formatShareResult(
        result: RemainderXResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val mixedNumber = formatMixedNumber(result.quotient, result.remainder, result.divisor)
        val decimalStr = formatDecimal(result.decimalResult, decimalPlaces, roundingMode)
        val verificationStr = formatVerification(
            result.dividend,
            result.divisor,
            result.quotient,
            result.remainder
        )

        return """
            RemainderX Division Result:
            ${result.dividend} ÷ ${result.divisor}
            Quotient: ${result.quotient}
            Remainder: ${result.remainder} (Absolute: ${result.absoluteRemainder})
            Mixed Number: $mixedNumber
            Decimal: $decimalStr

            Verification:
            $verificationStr (✓ Verified)
        """.trimIndent()
    }
}
