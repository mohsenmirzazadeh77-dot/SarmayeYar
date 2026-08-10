package com.sarmayeyar.app.model

data class Asset(
    val id: Long = 0L,
    val name: String,
    val type: String,
    val amount: Double,
    val unit: String,
    val buyPriceToman: Long,
    val currentPriceToman: Long,
    val isLivePrice: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val currentValue: Long
        get() = if (isLivePrice) (amount * currentPriceToman).toLong() else currentPriceToman

    val investedValue: Long
        get() = (amount * buyPriceToman).toLong()

    val profit: Long
        get() = currentValue - investedValue
}
