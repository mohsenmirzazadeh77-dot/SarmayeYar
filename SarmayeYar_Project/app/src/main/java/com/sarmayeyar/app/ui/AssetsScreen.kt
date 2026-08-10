package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters

@Composable
fun AssetsScreen(
    assets: List<Asset>,
    onAdd: (Asset) -> Unit,
    onDelete: (Asset) -> Unit
) {
    var show by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("دارایی‌ها", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { show = true }) { Text("+ افزودن") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(assets) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(a.name, style = MaterialTheme.typography.titleMedium)
                            Text("${a.type} • ${Formatters.number(a.amount)} ${a.unit}")
                            Text(Formatters.toman(a.currentValue))
                        }
                        TextButton(onClick = { onDelete(a) }) { Text("حذف") }
                    }
                }
            }
        }
    }

    if (show) {
        AddAssetDialog(
            onDismiss = { show = false },
            onSave = { onAdd(it); show = false }
        )
    }
}

@Composable
private fun AddAssetDialog(onDismiss: () -> Unit, onSave: (Asset) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("نقد") }
    var amount by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var buy by remember { mutableStateOf("") }

    val types = listOf("نقد", "طلا", "تتر", "بورس", "صندوق", "ارز", "سایر")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن دارایی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("نام") })
                Text("نوع")
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.take(4).forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.drop(4).forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t) })
                    }
                }
                OutlinedTextField(amount, { amount = it }, label = { Text("تعداد/واحد") })
                OutlinedTextField(buy, { buy = it }, label = { Text("قیمت خرید هر واحد (تومان)") })
                OutlinedTextField(price, { price = it }, label = { Text("قیمت فعلی هر واحد (تومان)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val a = amount.replace(",", "").toDoubleOrNull() ?: 0.0
                val b = buy.replace(",", "").toLongOrNull() ?: 0L
                val p = price.replace(",", "").toLongOrNull() ?: 0L
                if (name.isNotBlank() && a > 0 && p > 0) {
                    onSave(
                        Asset(
                            name = name,
                            type = type,
                            amount = a,
                            unit = if (type == "طلا") "گرم" else "واحد",
                            buyPriceToman = b,
                            currentPriceToman = p,
                            isLivePrice = type == "طلا" || type == "تتر"
                        )
                    )
                }
            }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
