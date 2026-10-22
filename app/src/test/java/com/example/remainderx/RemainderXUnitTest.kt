package com.example.remainderx

import com.example.remainderx.data.repository.SharedPreferencesHistoryRepository
import com.example.remainderx.data.repository.SharedPreferencesSettingsRepository
import com.example.remainderx.domain.RemainderXFormatter
import com.example.remainderx.domain.RemainderXParser
import com.example.remainderx.domain.RemainderXValidator
import com.example.remainderx.domain.ValidationResult
import com.example.remainderx.domain.math.RemainderXCalculator
import com.example.remainderx.model.AppRoundingMode
import com.example.remainderx.model.AppSettings
import com.example.remainderx.model.AppTheme
import com.example.remainderx.model.RemainderXHistory
import com.example.remainderx.model.RemainderXResult
import com.example.remainderx.testutil.FakeSharedPreferences
import com.example.remainderx.ui.RemainderXViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

class RemainderXUnitTest {

    private lateinit var fakeHistoryPrefs: FakeSharedPreferences
    private lateinit var fakeSettingsPrefs: FakeSharedPreferences
    private lateinit var historyRepo: SharedPreferencesHistoryRepository
    private lateinit var settingsRepo: SharedPreferencesSettingsRepository

    @Before
    fun setUp() {
        fakeHistoryPrefs = FakeSharedPreferences()
        fakeSettingsPrefs = FakeSharedPreferences()
        historyRepo = SharedPreferencesHistoryRepository(fakeHistoryPrefs)
        settingsRepo = SharedPreferencesSettingsRepository(fakeSettingsPrefs)
    }

    // 1. Integer parsing
    @Test
    fun test01_integerParsing() {
        val resultPositive = RemainderXParser.parseInteger("100")
        assertTrue(resultPositive.isSuccess)
        assertEquals(BigInteger("100"), resultPositive.getOrThrow())

        val resultExplicitPositive = RemainderXParser.parseInteger("+100")
        assertTrue(resultExplicitPositive.isSuccess)
        assertEquals(BigInteger("100"), resultExplicitPositive.getOrThrow())

        val resultZero = RemainderXParser.parseInteger("0")
        assertTrue(resultZero.isSuccess)
        assertEquals(BigInteger.ZERO, resultZero.getOrThrow())

        val resultNegative = RemainderXParser.parseInteger("-100")
        assertTrue(resultNegative.isSuccess)
        assertEquals(BigInteger("-100"), resultNegative.getOrThrow())
    }

    // 2. Empty input
    @Test
    fun test02_emptyInput() {
        val validation = RemainderXValidator.validateInputs("", "5")
        assertTrue(validation is ValidationResult.Error)
        assertEquals("Enter the dividend.", (validation as ValidationResult.Error).message)

        val validationDivisor = RemainderXValidator.validateInputs("17", "")
        assertTrue(validationDivisor is ValidationResult.Error)
        assertEquals("Enter the divisor.", (validationDivisor as ValidationResult.Error).message)

        val parserResult = RemainderXParser.parseInteger("")
        assertTrue(parserResult.isFailure)
        assertEquals("Enter a valid whole number.", parserResult.exceptionOrNull()?.message)
    }

    // 3. Invalid input
    @Test
    fun test03_invalidInput() {
        val invalidInputs = listOf("abc", "--", "++", "1 000", "10 00", "1E5", "!@#")
        for (input in invalidInputs) {
            val result = RemainderXParser.parseInteger(input)
            assertTrue("Expected failure for '$input'", result.isFailure)
            assertEquals("Enter a valid whole number.", result.exceptionOrNull()?.message)
        }
    }

    // 4. Decimal rejection
    @Test
    fun test04_decimalRejection() {
        val decimalInputs = listOf("10.5", "1.2.3", "0.0", "-5.2", ".5")
        for (input in decimalInputs) {
            val result = RemainderXParser.parseInteger(input)
            assertTrue("Expected failure for decimal '$input'", result.isFailure)
            assertEquals("Enter a valid whole number.", result.exceptionOrNull()?.message)
        }
    }

    // 5. Positive dividend
    @Test
    fun test05_positiveDividend() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger("17"), BigInteger("5"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger("17"), BigInteger("5"))
        assertEquals(BigInteger("3"), q)
        assertEquals(BigInteger("2"), r)
    }

    // 6. Positive divisor
    @Test
    fun test06_positiveDivisor() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger("23"), BigInteger("4"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger("23"), BigInteger("4"))
        assertEquals(BigInteger("5"), q)
        assertEquals(BigInteger("3"), r)
    }

    // 7. Negative dividend
    @Test
    fun test07_negativeDividend() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger("-17"), BigInteger("5"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger("-17"), BigInteger("5"))
        assertEquals(BigInteger("-3"), q)
        assertEquals(BigInteger("-2"), r)
        assertTrue(RemainderXCalculator.verifyCalculation(BigInteger("-17"), BigInteger("5"), q, r))
    }

    // 8. Negative divisor
    @Test
    fun test08_negativeDivisor() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger("17"), BigInteger("-5"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger("17"), BigInteger("-5"))
        assertEquals(BigInteger("-3"), q)
        assertEquals(BigInteger("2"), r)
        assertTrue(RemainderXCalculator.verifyCalculation(BigInteger("17"), BigInteger("-5"), q, r))
    }

    // 9. Both negative
    @Test
    fun test09_bothNegative() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger("-17"), BigInteger("-5"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger("-17"), BigInteger("-5"))
        assertEquals(BigInteger("3"), q)
        assertEquals(BigInteger("-2"), r)
        assertTrue(RemainderXCalculator.verifyCalculation(BigInteger("-17"), BigInteger("-5"), q, r))
    }

    // 10. Zero dividend
    @Test
    fun test10_zeroDividend() {
        val q = RemainderXCalculator.calculateQuotient(BigInteger.ZERO, BigInteger("5"))
        val r = RemainderXCalculator.calculateRemainder(BigInteger.ZERO, BigInteger("5"))
        val dec = RemainderXCalculator.calculateDecimalResult(BigInteger.ZERO, BigInteger("5"), MathContext(50, RoundingMode.HALF_UP))
        assertEquals(BigInteger.ZERO, q)
        assertEquals(BigInteger.ZERO, r)
        assertEquals("0", RemainderXFormatter.formatDecimal(dec, 4, RoundingMode.HALF_UP))
        assertTrue(RemainderXCalculator.verifyCalculation(BigInteger.ZERO, BigInteger("5"), q, r))
    }

    // 11. Divisor of one
    @Test
    fun test11_divisorOfOne() {
        val dividend = BigInteger("12345")
        val divisor = BigInteger.ONE
        val q = RemainderXCalculator.calculateQuotient(dividend, divisor)
        val r = RemainderXCalculator.calculateRemainder(dividend, divisor)
        assertEquals(BigInteger("12345"), q)
        assertEquals(BigInteger.ZERO, r)
        assertTrue(RemainderXCalculator.isExactDivision(r))
    }

    // 12. Exact division
    @Test
    fun test12_exactDivision() {
        val dividend = BigInteger("20")
        val divisor = BigInteger("5")
        val q = RemainderXCalculator.calculateQuotient(dividend, divisor)
        val r = RemainderXCalculator.calculateRemainder(dividend, divisor)
        assertEquals(BigInteger("4"), q)
        assertEquals(BigInteger.ZERO, r)
        assertTrue(RemainderXCalculator.isExactDivision(r))
        assertTrue(RemainderXCalculator.verifyCalculation(dividend, divisor, q, r))
    }

    // 13. Non-exact division
    @Test
    fun test13_nonExactDivision() {
        val dividend = BigInteger("23")
        val divisor = BigInteger("4")
        val q = RemainderXCalculator.calculateQuotient(dividend, divisor)
        val r = RemainderXCalculator.calculateRemainder(dividend, divisor)
        assertEquals(BigInteger("5"), q)
        assertEquals(BigInteger("3"), r)
        assertFalse(RemainderXCalculator.isExactDivision(r))
        assertTrue(RemainderXCalculator.verifyCalculation(dividend, divisor, q, r))
    }

    // 14. Division by zero
    @Test
    fun test14_divisionByZero() {
        val validation = RemainderXValidator.validateInputs("17", "0")
        assertTrue(validation is ValidationResult.Error)
        assertEquals("A number cannot be divided by zero.", (validation as ValidationResult.Error).message)

        try {
            RemainderXCalculator.calculateQuotient(BigInteger("17"), BigInteger.ZERO)
            org.junit.Assert.fail("Should have thrown ArithmeticException or IllegalArgumentException")
        } catch (e: Exception) {
            assertEquals("A number cannot be divided by zero.", e.message)
        }
    }

    // 15. Quotient calculation
    @Test
    fun test15_quotientCalculation() {
        assertEquals(BigInteger("3"), RemainderXCalculator.calculateQuotient(BigInteger("17"), BigInteger("5")))
        assertEquals(BigInteger("-4"), RemainderXCalculator.calculateQuotient(BigInteger("-20"), BigInteger("5")))
        assertEquals(BigInteger("0"), RemainderXCalculator.calculateQuotient(BigInteger("2"), BigInteger("5")))
    }

    // 16. Remainder calculation
    @Test
    fun test16_remainderCalculation() {
        assertEquals(BigInteger("2"), RemainderXCalculator.calculateRemainder(BigInteger("17"), BigInteger("5")))
        assertEquals(BigInteger("-2"), RemainderXCalculator.calculateRemainder(BigInteger("-17"), BigInteger("5")))
        assertEquals(BigInteger("0"), RemainderXCalculator.calculateRemainder(BigInteger("20"), BigInteger("5")))
    }

    // 17. Absolute remainder
    @Test
    fun test17_absoluteRemainder() {
        assertEquals(BigInteger("2"), RemainderXCalculator.calculateAbsoluteRemainder(BigInteger("2")))
        assertEquals(BigInteger("2"), RemainderXCalculator.calculateAbsoluteRemainder(BigInteger("-2")))
        assertEquals(BigInteger.ZERO, RemainderXCalculator.calculateAbsoluteRemainder(BigInteger.ZERO))
    }

    // 18. Decimal result
    @Test
    fun test18_decimalResult() {
        val mathContext = MathContext(50, RoundingMode.HALF_UP)

        val dec1 = RemainderXCalculator.calculateDecimalResult(BigInteger("17"), BigInteger("5"), mathContext)
        assertEquals("3.4", RemainderXFormatter.formatDecimal(dec1, 4, RoundingMode.HALF_UP))

        val dec2 = RemainderXCalculator.calculateDecimalResult(BigInteger("23"), BigInteger("4"), mathContext)
        assertEquals("5.75", RemainderXFormatter.formatDecimal(dec2, 4, RoundingMode.HALF_UP))

        val dec3 = RemainderXCalculator.calculateDecimalResult(BigInteger("10"), BigInteger("3"), mathContext)
        assertEquals("3.3333", RemainderXFormatter.formatDecimal(dec3, 4, RoundingMode.HALF_UP))
    }

    // 19. Mixed-number result
    @Test
    fun test19_mixedNumberResult() {
        assertEquals("3 2/5", RemainderXFormatter.formatMixedNumber(BigInteger("3"), BigInteger("2"), BigInteger("5")))
        assertEquals("4", RemainderXFormatter.formatMixedNumber(BigInteger("4"), BigInteger.ZERO, BigInteger("5")))
        assertEquals("2/5", RemainderXFormatter.formatMixedNumber(BigInteger.ZERO, BigInteger("2"), BigInteger("5")))
        assertEquals("-3 2/5", RemainderXFormatter.formatMixedNumber(BigInteger("-3"), BigInteger("-2"), BigInteger("5")))
    }

    // 20. Verification equation
    @Test
    fun test20_verificationEquation() {
        val equation = RemainderXFormatter.formatVerification(
            BigInteger("17"),
            BigInteger("5"),
            BigInteger("3"),
            BigInteger("2")
        )
        assertEquals("5 × 3 + 2 = 17", equation)
        assertTrue(RemainderXCalculator.verifyCalculation(BigInteger("17"), BigInteger("5"), BigInteger("3"), BigInteger("2")))
    }

    // 21. Very large integers
    @Test
    fun test21_veryLargeIntegers() {
        val dividend = BigInteger("999999999999999999999999999999999999999999999")
        val divisor = BigInteger("123456789")

        val q = RemainderXCalculator.calculateQuotient(dividend, divisor)
        val r = RemainderXCalculator.calculateRemainder(dividend, divisor)

        assertTrue(q > BigInteger.ZERO)
        assertTrue(r < divisor)
        assertTrue(RemainderXCalculator.verifyCalculation(dividend, divisor, q, r))
    }

    // 22. Very large negative integers
    @Test
    fun test22_veryLargeNegativeIntegers() {
        val dividend = BigInteger("-999999999999999999999999999999999999999999999")
        val divisor = BigInteger("123456789")

        val q = RemainderXCalculator.calculateQuotient(dividend, divisor)
        val r = RemainderXCalculator.calculateRemainder(dividend, divisor)

        assertTrue(q < BigInteger.ZERO)
        assertTrue(r < BigInteger.ZERO)
        assertEquals(r.abs(), RemainderXCalculator.calculateAbsoluteRemainder(r))
        assertTrue(RemainderXCalculator.verifyCalculation(dividend, divisor, q, r))
    }

    // 23. BigInteger precision
    @Test
    fun test23_bigIntegerPrecision() {
        // Exceeds 64-bit Long.MAX_VALUE (approx 9.22 * 10^18)
        val huge1 = BigInteger("10").pow(40).multiply(BigInteger("7"))
        val huge2 = BigInteger("7")

        val q = RemainderXCalculator.calculateQuotient(huge1, huge2)
        val r = RemainderXCalculator.calculateRemainder(huge1, huge2)

        assertEquals(BigInteger("10").pow(40), q)
        assertEquals(BigInteger.ZERO, r)
        assertTrue(RemainderXCalculator.isExactDivision(r))
    }

    // 24. No overflow
    @Test
    fun test24_noOverflow() {
        val maxLongPlus = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN)
        val q = RemainderXCalculator.calculateQuotient(maxLongPlus, BigInteger.TEN)
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE), q)
    }

    // 25. Display rounding
    @Test
    fun test25_displayRounding() {
        val value = BigDecimal("3.14159265")
        val halfUp2 = RemainderXFormatter.formatDecimal(value, 2, RoundingMode.HALF_UP)
        assertEquals("3.14", halfUp2)

        val halfUp4 = RemainderXFormatter.formatDecimal(value, 4, RoundingMode.HALF_UP)
        assertEquals("3.1416", halfUp4)

        val floor4 = RemainderXFormatter.formatDecimal(value, 4, RoundingMode.FLOOR)
        assertEquals("3.1415", floor4)

        val places0 = RemainderXFormatter.formatDecimal(value, 0, RoundingMode.HALF_UP)
        assertEquals("3", places0)
    }

    // 26. Calculation steps
    @Test
    fun test26_calculationSteps() {
        val steps = RemainderXCalculator.generateCalculationSteps(
            BigInteger("17"),
            BigInteger("5"),
            BigInteger("3"),
            BigInteger("2")
        )
        assertEquals(4, steps.size)
        assertEquals("Multiplication", steps[0].label)
        assertEquals("5 × 3", steps[0].expression)
        assertEquals("15", steps[0].result)

        assertEquals("Subtraction", steps[1].label)
        assertEquals("17 − 15", steps[1].expression)
        assertEquals("2", steps[1].result)

        assertEquals("Quotient & Remainder", steps[2].label)
        assertEquals("Has a remainder", steps[2].result)

        assertEquals("Verification", steps[3].label)
        assertEquals("= 17", steps[3].result)
    }

    // 27. History save
    @Test
    fun test27_historySave() {
        val saved = historyRepo.saveCalculation("17", "5")
        assertTrue(saved)
        val history = historyRepo.getHistory()
        assertEquals(1, history.size)
        assertEquals("17", history[0].dividend)
        assertEquals("5", history[0].divisor)
    }

    // 28. History restore
    @Test
    fun test28_historyRestore() {
        val viewModel = RemainderXViewModel(historyRepo, settingsRepo)
        val historyItem = RemainderXHistory(
            id = "test-1",
            dividend = "20",
            divisor = "5",
            savedAt = System.currentTimeMillis()
        )
        viewModel.restoreHistoryItem(historyItem)
        val state = viewModel.uiState.value
        assertEquals("20", state.dividendInput)
        assertEquals("5", state.divisorInput)
        assertNotNull(state.result)
        assertEquals(BigInteger("4"), state.result?.quotient)
        assertEquals(BigInteger.ZERO, state.result?.remainder)
    }

    // 29. History limit
    @Test
    fun test29_historyLimit() {
        for (i in 1..15) {
            historyRepo.saveCalculation(i.toString(), (i + 1).toString())
        }
        val history = historyRepo.getHistory()
        assertEquals(10, history.size)
        // Latest saved was 15, oldest in top 10 is 6
        assertEquals("15", history.first().dividend)
        assertEquals("6", history.last().dividend)
    }

    // 30. Duplicate history
    @Test
    fun test30_duplicateHistory() {
        val firstSave = historyRepo.saveCalculation("17", "5")
        assertTrue(firstSave)
        val duplicateSave = historyRepo.saveCalculation("17", "5")
        assertFalse(duplicateSave)
        assertEquals(1, historyRepo.getHistory().size)
    }

    // 31. History deletion
    @Test
    fun test31_historyDeletion() {
        historyRepo.saveCalculation("17", "5")
        historyRepo.saveCalculation("23", "4")
        val list = historyRepo.getHistory()
        assertEquals(2, list.size)

        val deleted = historyRepo.deleteHistoryItem(list[0].id)
        assertTrue(deleted)
        assertEquals(1, historyRepo.getHistory().size)

        historyRepo.clearHistory()
        assertTrue(historyRepo.getHistory().isEmpty())
    }

    // 32. Settings persistence
    @Test
    fun test32_settingsPersistence() {
        val modifiedSettings = AppSettings(
            theme = AppTheme.DARK,
            dynamicColor = false,
            hapticFeedback = false,
            animations = false,
            confirmDelete = false,
            decimalPlaces = 2,
            roundingMode = AppRoundingMode.HALF_EVEN
        )
        settingsRepo.updateSettings(modifiedSettings)

        val reloadedSettings = settingsRepo.getSettings()
        assertEquals(AppTheme.DARK, reloadedSettings.theme)
        assertFalse(reloadedSettings.dynamicColor)
        assertFalse(reloadedSettings.hapticFeedback)
        assertFalse(reloadedSettings.animations)
        assertFalse(reloadedSettings.confirmDelete)
        assertEquals(2, reloadedSettings.decimalPlaces)
        assertEquals(AppRoundingMode.HALF_EVEN, reloadedSettings.roundingMode)
    }

    // 33. Settings reset
    @Test
    fun test33_settingsReset() {
        settingsRepo.updateSettings(
            AppSettings(
                theme = AppTheme.DARK,
                decimalPlaces = 8,
                roundingMode = AppRoundingMode.CEILING
            )
        )
        // Save a history item
        historyRepo.saveCalculation("17", "5")

        val reset = settingsRepo.resetSettings()
        assertEquals(AppTheme.SYSTEM, reset.theme)
        assertTrue(reset.dynamicColor)
        assertTrue(reset.hapticFeedback)
        assertTrue(reset.animations)
        assertTrue(reset.confirmDelete)
        assertEquals(4, reset.decimalPlaces)
        assertEquals(AppRoundingMode.HALF_UP, reset.roundingMode)

        // Verify history remains intact after settings reset
        assertEquals(1, historyRepo.getHistory().size)
    }

    // 34. Copy formatting
    @Test
    fun test34_copyFormatting() {
        val result = RemainderXResult(
            dividend = BigInteger("17"),
            divisor = BigInteger("5"),
            quotient = BigInteger("3"),
            remainder = BigInteger("2"),
            absoluteRemainder = BigInteger("2"),
            decimalResult = BigDecimal("3.4"),
            calculationSteps = RemainderXCalculator.generateCalculationSteps(
                BigInteger("17"),
                BigInteger("5"),
                BigInteger("3"),
                BigInteger("2")
            ),
            calculatedAt = System.currentTimeMillis()
        )

        val copyResult = RemainderXFormatter.formatCopyResult(result, 4, RoundingMode.HALF_UP)
        assertTrue(copyResult.contains("RemainderX"))
        assertTrue(copyResult.contains("Dividend: 17"))
        assertTrue(copyResult.contains("Divisor: 5"))
        assertTrue(copyResult.contains("Quotient: 3"))
        assertTrue(copyResult.contains("Remainder: 2"))
        assertTrue(copyResult.contains("Absolute Remainder: 2"))
        assertTrue(copyResult.contains("Decimal Result: 3.4"))
        assertTrue(copyResult.contains("5 × 3 + 2 = 17"))

        val copyCalc = RemainderXFormatter.formatCopyCalculation(result)
        assertTrue(copyCalc.contains("17 ÷ 5"))
        assertTrue(copyCalc.contains("5 × 3 = 15"))
        assertTrue(copyCalc.contains("17 − 15 = 2"))
        assertTrue(copyCalc.contains("5 × 3 + 2 = 17"))
    }

    // 35. Share formatting
    @Test
    fun test35_shareFormatting() {
        val result = RemainderXResult(
            dividend = BigInteger("17"),
            divisor = BigInteger("5"),
            quotient = BigInteger("3"),
            remainder = BigInteger("2"),
            absoluteRemainder = BigInteger("2"),
            decimalResult = BigDecimal("3.4"),
            calculationSteps = RemainderXCalculator.generateCalculationSteps(
                BigInteger("17"),
                BigInteger("5"),
                BigInteger("3"),
                BigInteger("2")
            ),
            calculatedAt = System.currentTimeMillis()
        )

        val shareText = RemainderXFormatter.formatShareResult(result, 4, RoundingMode.HALF_UP)
        assertTrue(shareText.contains("RemainderX Division Result:"))
        assertTrue(shareText.contains("17 ÷ 5"))
        assertTrue(shareText.contains("Quotient: 3"))
        assertTrue(shareText.contains("Remainder: 2"))
        assertTrue(shareText.contains("Mixed Number: 3 2/5"))
        assertTrue(shareText.contains("Decimal: 3.4"))
        assertTrue(shareText.contains("5 × 3 + 2 = 17 (✓ Verified)"))
    }

    // Extra Critical Test: 500-character input length limit
    @Test
    fun testExtra_inputLengthLimit() {
        val valid500 = "9".repeat(500)
        val parse500 = RemainderXParser.parseInteger(valid500)
        assertTrue(parse500.isSuccess)

        val invalid501 = "9".repeat(501)
        val parse501 = RemainderXParser.parseInteger(invalid501)
        assertTrue(parse501.isFailure)
        assertEquals("Number is too long. Maximum 500 characters.", parse501.exceptionOrNull()?.message)
    }

    // Extra Critical Test: Swap & Clear in ViewModel
    @Test
    fun testExtra_viewModelSwapAndClear() {
        val viewModel = RemainderXViewModel(historyRepo, settingsRepo)
        viewModel.onDividendChanged("17")
        viewModel.onDivisorChanged("5")
        viewModel.calculate()
        assertNotNull(viewModel.uiState.value.result)

        viewModel.swapInputs()
        assertEquals("5", viewModel.uiState.value.dividendInput)
        assertEquals("17", viewModel.uiState.value.divisorInput)
        assertNull(viewModel.uiState.value.result)

        viewModel.clearInputs()
        assertEquals("", viewModel.uiState.value.dividendInput)
        assertEquals("", viewModel.uiState.value.divisorInput)
        assertNull(viewModel.uiState.value.result)
    }
}
