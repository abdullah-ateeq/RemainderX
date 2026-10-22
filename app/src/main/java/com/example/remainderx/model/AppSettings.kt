package com.example.remainderx.model

import java.math.RoundingMode

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppRoundingMode(val roundingMode: RoundingMode, val displayName: String) {
    HALF_UP(RoundingMode.HALF_UP, "Half Up"),
    HALF_DOWN(RoundingMode.HALF_DOWN, "Half Down"),
    HALF_EVEN(RoundingMode.HALF_EVEN, "Half Even"),
    UP(RoundingMode.UP, "Up"),
    DOWN(RoundingMode.DOWN, "Down"),
    CEILING(RoundingMode.CEILING, "Ceiling"),
    FLOOR(RoundingMode.FLOOR, "Floor");

    companion object {
        fun fromName(name: String?): AppRoundingMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: HALF_UP
        }
    }
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val decimalPlaces: Int = 4,
    val roundingMode: AppRoundingMode = AppRoundingMode.HALF_UP
)
