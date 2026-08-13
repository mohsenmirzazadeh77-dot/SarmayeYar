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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBackup: () -> String
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
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "واحد پول: تومان"
                )

                Text(
                    text =
                        "نمایش: ارزش ریالی / تعداد واحد"
                )

                Text(
                    text =
                        "حالت امنیتی: بدون قفل"
                )
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
                    style =
                        MaterialTheme.typography.titleMedium
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
                        Text(
                            text = "حالت تاریک"
                        )

                        Text(
                            text =
                                "تغییر ظاهر برنامه به حالت تاریک"
                        )
                    }

                    Switch(
                        checked = false,
                        onCheckedChange = {
                            /*
                             * کنترل اصلی حالت تاریک
                             * در Theme مدیریت می‌شود.
                             */
                        }
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                val text = onBackup()

                val intent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            text
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
            style =
                MaterialTheme.typography.titleMedium
        )

        Text(
            text = "نسخه 1.0.0"
        )
    }
}
