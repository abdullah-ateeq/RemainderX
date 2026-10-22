package com.example.remainderx.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.remainderx.model.AppRoundingMode
import com.example.remainderx.model.AppSettings
import com.example.remainderx.model.AppTheme

interface SettingsRepository {
    fun getSettings(): AppSettings
    fun updateSettings(settings: AppSettings)
    fun resetSettings(): AppSettings
    fun getLastInputs(): Pair<String, String>
    fun saveLastInputs(dividend: String, divisor: String)
}

class SharedPreferencesSettingsRepository(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val PREF_NAME = "remainderx_settings"
        private const val KEY_THEME = "key_theme"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_CONFIRM_DELETE = "key_confirm_delete"
        private const val KEY_DECIMAL_PLACES = "key_decimal_places"
        private const val KEY_ROUNDING_MODE = "key_rounding_mode"
        private const val KEY_LAST_DIVIDEND = "key_last_dividend"
        private const val KEY_LAST_DIVISOR = "key_last_divisor"

        fun create(context: Context): SharedPreferencesSettingsRepository {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return SharedPreferencesSettingsRepository(prefs)
        }
    }

    override fun getSettings(): AppSettings {
        val themeStr = sharedPreferences.getString(KEY_THEME, AppTheme.SYSTEM.name)
        val theme = try {
            AppTheme.valueOf(themeStr ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }

        val dynamicColor = sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, true)
        val haptic = sharedPreferences.getBoolean(KEY_HAPTIC, true)
        val animations = sharedPreferences.getBoolean(KEY_ANIMATIONS, true)
        val confirmDelete = sharedPreferences.getBoolean(KEY_CONFIRM_DELETE, true)
        val decimalPlaces = sharedPreferences.getInt(KEY_DECIMAL_PLACES, 4)
        val roundingModeStr = sharedPreferences.getString(KEY_ROUNDING_MODE, AppRoundingMode.HALF_UP.name)
        val roundingMode = AppRoundingMode.fromName(roundingModeStr)

        return AppSettings(
            theme = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = haptic,
            animations = animations,
            confirmDelete = confirmDelete,
            decimalPlaces = decimalPlaces,
            roundingMode = roundingMode
        )
    }

    override fun updateSettings(settings: AppSettings) {
        sharedPreferences.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putBoolean(KEY_HAPTIC, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putInt(KEY_DECIMAL_PLACES, settings.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            .apply()
    }

    override fun resetSettings(): AppSettings {
        val defaultSettings = AppSettings()
        updateSettings(defaultSettings)
        return defaultSettings
    }

    override fun getLastInputs(): Pair<String, String> {
        val dividend = sharedPreferences.getString(KEY_LAST_DIVIDEND, "") ?: ""
        val divisor = sharedPreferences.getString(KEY_LAST_DIVISOR, "") ?: ""
        return Pair(dividend, divisor)
    }

    override fun saveLastInputs(dividend: String, divisor: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_DIVIDEND, dividend)
            .putString(KEY_LAST_DIVISOR, divisor)
            .apply()
    }
}
