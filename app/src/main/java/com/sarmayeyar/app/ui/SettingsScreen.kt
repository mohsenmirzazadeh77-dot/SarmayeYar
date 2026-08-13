package com.sarmayeyar.app.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBackup: () -> String,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "تنظیمات",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("واحد پول: تومان")
                Text("نمایش: ارزش ریالی / تعداد واحد")
                Text("حالت امنیتی: بدون قفل")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "ظاهر برنامه",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حالت تاریک")

                        Text(
                            if (darkMode) {
                                "حالت تاریک فعال است"
                            } else {
                                "حالت روشن فعال است"
                            }
                        )
                    }

                    Switch(
                        checked = darkMode,
                        onCheckedChange = onDarkModeChange
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                val backupText = onBackup()

                val intent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"

                        putExtra(
                            Intent.EXTRA_TEXT,
                            backupText
                        )
                    }

                context.startActivity(
                    Intent.createChooser(
                        intent,
                        "ارسال پشتیبان"
                    )
                )
            }
        ) {
            Text("پشتیبان‌گیری")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "SarmayeYar",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "نسخه 1.0.0"
        )
    }
}
