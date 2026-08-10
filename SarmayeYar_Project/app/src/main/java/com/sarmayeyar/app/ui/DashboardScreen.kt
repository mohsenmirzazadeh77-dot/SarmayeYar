package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.network.LivePrices
import com.sarmayeyar.app.util.Formatters

@Composable
fun DashboardScreen(
    assets: List<Asset>,
    prices: LivePrices,
    onRefresh: () -> Unit,
    busy: Boolean
) {
    val total = assets.sumOf { it.currentValue }
    val invested = assets.sumOf { it.investedValue }
    val profit = total - invested

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("SarmayeYar", style = MaterialTheme.typography.headlineLarge)
            Text("سرمایه‌یار", style = MaterialTheme.typography.titleMedium)
        }
        item { MoneyCard(total, profit) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onRefresh, enabled = !busy, modifier = Modifier.weight(1f)) {
                    Text(if (busy) "در حال دریافت..." else "به‌روزرسانی قیمت")
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("قیمت‌های آنلاین", style = MaterialTheme.typography.titleMedium)
                    Text("تتر: ${prices.usdtToman?.let { Formatters.toman(it) } ?: "—"}")
                    Text("طلای ۱۸: ${prices.gold18TomanPerGram?.let { Formatters.toman(it) } ?: "—"} / گرم")
                }
            }
        }
        item { Text("دارایی‌ها", style = MaterialTheme.typography.titleLarge) }
        items(assets) { asset ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(asset.name, style = MaterialTheme.typography.titleMedium)
                    Text("${asset.type} • ${Formatters.number(asset.amount)} ${asset.unit}")
                    Text(Formatters.toman(asset.currentValue))
                    val share = if (total > 0) asset.currentValue * 100.0 / total else 0.0
                    Text("سهم: ${Formatters.percent(share)}")
                }
            }
        }
        if (assets.isEmpty()) {
            item { Text("هنوز دارایی ثبت نشده است. از بخش «دارایی‌ها» اولین مورد را اضافه کنید.") }
        }
    }
}
