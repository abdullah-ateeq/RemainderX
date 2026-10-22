package com.example.remainderx.model

/**
 * Historical record of inputs saved by the user.
 * Values are stored as strings to preserve exact formatting and BigInteger representation.
 */
data class RemainderXHistory(
    val id: String,
    val dividend: String,
    val divisor: String,
    val savedAt: Long
)
