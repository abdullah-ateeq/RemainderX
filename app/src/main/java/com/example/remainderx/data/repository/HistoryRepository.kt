package com.example.remainderx.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.remainderx.model.RemainderXHistory
import java.util.UUID

interface HistoryRepository {
    fun getHistory(): List<RemainderXHistory>
    fun saveCalculation(dividend: String, divisor: String): Boolean
    fun deleteHistoryItem(id: String): Boolean
    fun clearHistory()
}

class SharedPreferencesHistoryRepository(
    private val sharedPreferences: SharedPreferences
) : HistoryRepository {

    companion object {
        private const val PREF_KEY_HISTORY = "pref_key_history_v2"
        const val MAX_HISTORY_LIMIT = 10
        private const val FIELD_DELIMITER = "\t"
        private const val ROW_DELIMITER = "\n"

        fun create(context: Context): SharedPreferencesHistoryRepository {
            val prefs = context.getSharedPreferences("remainderx_history", Context.MODE_PRIVATE)
            return SharedPreferencesHistoryRepository(prefs)
        }
    }

    @Synchronized
    override fun getHistory(): List<RemainderXHistory> {
        val rawData = sharedPreferences.getString(PREF_KEY_HISTORY, null) ?: return emptyList()
        if (rawData.isBlank()) return emptyList()

        val list = mutableListOf<RemainderXHistory>()
        val rows = rawData.split(ROW_DELIMITER)
        for (row in rows) {
            val trimmed = row.trim()
            if (trimmed.isEmpty()) continue
            val parts = trimmed.split(FIELD_DELIMITER)
            if (parts.size >= 4) {
                val savedAt = parts[3].toLongOrNull() ?: 0L
                list.add(
                    RemainderXHistory(
                        id = parts[0],
                        dividend = parts[1],
                        divisor = parts[2],
                        savedAt = savedAt
                    )
                )
            }
        }
        return list
    }

    @Synchronized
    override fun saveCalculation(dividend: String, divisor: String): Boolean {
        val currentList = getHistory().toMutableList()

        // Check if exact same dividend/divisor pair is saved consecutively (at top)
        if (currentList.isNotEmpty()) {
            val latest = currentList.first()
            if (latest.dividend == dividend && latest.divisor == divisor) {
                return false
            }
        }

        val newItem = RemainderXHistory(
            id = UUID.randomUUID().toString(),
            dividend = dividend,
            divisor = divisor,
            savedAt = System.currentTimeMillis()
        )

        // Add to the top
        currentList.add(0, newItem)

        // Enforce limit of 10 items
        while (currentList.size > MAX_HISTORY_LIMIT) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveToPrefs(currentList)
        return true
    }

    @Synchronized
    override fun deleteHistoryItem(id: String): Boolean {
        val currentList = getHistory().toMutableList()
        val removed = currentList.removeAll { it.id == id }
        if (removed) {
            saveToPrefs(currentList)
        }
        return removed
    }

    @Synchronized
    override fun clearHistory() {
        sharedPreferences.edit().remove(PREF_KEY_HISTORY).apply()
    }

    private fun saveToPrefs(list: List<RemainderXHistory>) {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item.id)
                .append(FIELD_DELIMITER)
                .append(item.dividend)
                .append(FIELD_DELIMITER)
                .append(item.divisor)
                .append(FIELD_DELIMITER)
                .append(item.savedAt)
                .append(ROW_DELIMITER)
        }
        sharedPreferences.edit().putString(PREF_KEY_HISTORY, sb.toString()).apply()
    }
}
