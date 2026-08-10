package com.sarmayeyar.app.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    fun toman(value: Long): String =
        NumberFormat.getNumberInstance(Locale.US).format(value) + " تومان"

    fun number(value: Double): String =
        NumberFormat.getNumberInstance(Locale.US).format(value)

    fun percent(value: Double): String =
        String.format(Locale.US, "%.1f%%", value)
}
