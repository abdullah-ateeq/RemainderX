package com.example.remainderx.domain

import java.math.BigInteger

sealed interface ValidationResult {
    data class Success(val dividend: BigInteger, val divisor: BigInteger) : ValidationResult
    data class Error(val message: String) : ValidationResult
}

object RemainderXValidator {

    fun validateInputs(dividendRaw: String, divisorRaw: String): ValidationResult {
        val trimmedDividend = dividendRaw.trim()
        val trimmedDivisor = divisorRaw.trim()

        if (trimmedDividend.isEmpty()) {
            return ValidationResult.Error(RemainderXParser.ERROR_DIVIDEND_EMPTY)
        }

        if (trimmedDividend.length > RemainderXParser.MAX_INPUT_LENGTH) {
            return ValidationResult.Error(RemainderXParser.ERROR_TOO_LONG)
        }

        val dividendParsed = RemainderXParser.parseInteger(trimmedDividend)
        if (dividendParsed.isFailure) {
            val exMsg = dividendParsed.exceptionOrNull()?.message
            return ValidationResult.Error(exMsg ?: RemainderXParser.ERROR_INVALID)
        }

        if (trimmedDivisor.isEmpty()) {
            return ValidationResult.Error(RemainderXParser.ERROR_DIVISOR_EMPTY)
        }

        if (trimmedDivisor.length > RemainderXParser.MAX_INPUT_LENGTH) {
            return ValidationResult.Error(RemainderXParser.ERROR_TOO_LONG)
        }

        val divisorParsed = RemainderXParser.parseInteger(trimmedDivisor)
        if (divisorParsed.isFailure) {
            val exMsg = divisorParsed.exceptionOrNull()?.message
            return ValidationResult.Error(exMsg ?: RemainderXParser.ERROR_INVALID)
        }

        val dividend = dividendParsed.getOrThrow()
        val divisor = divisorParsed.getOrThrow()

        if (divisor == BigInteger.ZERO) {
            return ValidationResult.Error(RemainderXParser.ERROR_DIVISION_BY_ZERO)
        }

        return ValidationResult.Success(dividend, divisor)
    }
}
