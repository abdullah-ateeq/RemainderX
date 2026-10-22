package com.example.remainderx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.remainderx.data.repository.SharedPreferencesHistoryRepository
import com.example.remainderx.data.repository.SharedPreferencesSettingsRepository
import com.example.remainderx.ui.RemainderXViewModel
import com.example.remainderx.ui.navigation.RemainderXApp
import com.example.remainderx.ui.theme.RemainderXTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RemainderXViewModel by viewModels {
        val historyRepo = SharedPreferencesHistoryRepository.create(applicationContext)
        val settingsRepo = SharedPreferencesSettingsRepository.create(applicationContext)
        RemainderXViewModel.Factory(historyRepo, settingsRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()
            RemainderXTheme(
                theme = settings.theme,
                dynamicColor = settings.dynamicColor
            ) {
                RemainderXApp(viewModel = viewModel)
            }
        }
    }
}
