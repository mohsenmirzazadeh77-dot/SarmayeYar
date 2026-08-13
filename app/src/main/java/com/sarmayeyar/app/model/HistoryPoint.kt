package com.sarmayeyar.app.model

data class HistoryPoint(
    val timestamp: Long,
    val totalToman: Long,
    val categoryValues: Map<String, Long> = emptyMap()
)
