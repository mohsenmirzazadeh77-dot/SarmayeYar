package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters

@Composable
fun ScenarioScreen(assets: List<Asset>) {
    var from by remember { mutableStateOf(assets.firstOrNull()?.name ?: "") }
    var to by remember { mutableStateOf(assets.getOrNull(1)?.name ?: "") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Asset>?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("سناریوساز", style = MaterialTheme.typography.headlineMedium)
        Text("قبل از انتقال سرمایه، اثر آن را روی سبد ببینید.")
        OutlinedTextField(from, { from = it }, label = { Text("از دارایی") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(to, { to = it }, label = { Text("به دارایی") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount, { amount = it }, label = { Text("مبلغ (تومان)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val x = amount.replace(",", "").toLongOrNull() ?: 0L
            result = assets.map {
                when (it.name) {
                    from -> it.copy(currentPriceToman = ((it.currentValue - x) / it.amount.coerceAtLeast(1.0)).toLong())
                    to -> it.copy(currentPriceToman = ((it.currentValue + x) / it.amount.coerceAtLeast(1.0)).toLong())
                    else -> it
                }
            }
        }) { Text("محاسبه سناریو") }

        result?.let { simulated ->
            Text("نتیجه", style = MaterialTheme.typography.titleLarge)
            val total = simulated.sumOf { it.currentValue }
            simulated.forEach { a ->
                val share = if (total > 0) a.currentValue * 100.0 / total else 0.0
                Text("${a.name}: ${Formatters.percent(share)}")
            }
        }
    }
}
