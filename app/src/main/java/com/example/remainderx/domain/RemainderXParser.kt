package com.example.remainderx.domain

import java.math.BigInteger

/**
 * Validates and parses raw text into BigInteger values.
 * Strictly accepts integer formats and rejects non-integer formats like decimals or scientific notation.
 */
object RemainderXParser {
    const val MAX_INPUT_LENGTH = 500
    const val ERROR_TOO_LONG = "Number is too long. Maximum 500 characters."
    const val ERROR_INVALID = "Enter a valid whole number."
    const val ERROR_DIVIDEND_EMPTY = "Enter the dividend."
    const val ERROR_DIVISOR_EMPTY = "Enter the divisor."
    const val ERROR_DIVISION_BY_ZERO = "A number cannot be divided by zero."

    private val INTEGER_REGEX = Regex("^[+-]?[0-9]+$")

    fun parseInteger(input: String): Result<BigInteger> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException(ERROR_INVALID))
        }

        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException(ERROR_TOO_LONG))
        }

        if (trimmed.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException(ERROR_INVALID))
        }

        if (!INTEGER_REGEX.matches(trimmed)) {
            return Result.failure(IllegalArgumentException(ERROR_INVALID))
        }

        return try {
            val parseString = if (trimmed.startsWith("+")) trimmed.substring(1) else trimmed
            Result.success(BigInteger(parseString))
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException(ERROR_INVALID))
        }
    }
}
