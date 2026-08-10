package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters

@Composable
fun AnalysisScreen(assets: List<Asset>) {
    val total = assets.sumOf { it.currentValue }
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("تحلیل سرمایه", style = MaterialTheme.typography.headlineMedium) }
        items(assets) { a ->
            val share = if (total > 0) a.currentValue * 100.0 / total else 0.0
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(a.name, style = MaterialTheme.typography.titleMedium)
                    Text("سهم فعلی: ${Formatters.percent(share)}")
                    Text("ارزش: ${Formatters.toman(a.currentValue)}")
                }
            }
        }
        if (assets.isEmpty()) item { Text("برای تحلیل، ابتدا دارایی ثبت کنید.") }
    }
}
