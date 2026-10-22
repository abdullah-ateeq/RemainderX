package com.example.remainderx.model

data class RemainderXUiState(
    val dividendInput: String = "",
    val divisorInput: String = "",
    val result: RemainderXResult? = null,
    val history: List<RemainderXHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false
)
