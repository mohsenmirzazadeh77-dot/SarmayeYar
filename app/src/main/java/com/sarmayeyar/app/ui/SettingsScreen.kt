package com.sarmayeyar.app.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onBackup: () -> String) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("واحد پول: تومان")
                Text("نمایش: ارزش ریالی / تعداد واحد")
                Text("حالت امنیتی: بدون قفل")
            }
        }
        Button(onClick = {
            val text = onBackup()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "ارسال پشتیبان"))
        }) {
            Text("پشتیبان‌گیری")
        }
        Text("نسخه 1.0.0")
    }
}
