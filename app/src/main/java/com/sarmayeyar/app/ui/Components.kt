package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.util.Formatters

@Composable
fun MoneyCard(total: Long, profit: Long) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("ارزش کل سرمایه", style = MaterialTheme.typography.labelLarge)
            Text(Formatters.toman(total), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "سود/زیان: ${Formatters.toman(profit)}",
                color = if (profit >= 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}
