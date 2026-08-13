package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.network.LivePrices
import com.sarmayeyar.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    assets: List<Asset>,
    prices: LivePrices,
    onRefresh: () -> Unit,
    busy: Boolean
) {
    /*
     * ارزش رسمی کل سرمایه
     *
     * این مقدار فقط از ارزش ثبت‌شده دارایی‌ها
     * محاسبه می‌شود و به قیمت آنلاین وابسته نیست.
     */
    val totalCapital = assets.sumOf { it.currentValue }

    /*
     * محاسبه ارزش لحظه‌ای
     *
     * برای طلا و تتر از قیمت آنلاین استفاده می‌کنیم.
     * سایر دارایی‌ها همان ارزش ثبت‌شده خودشان را حفظ می‌کنند.
     */
    val liveCapital = assets.sumOf { asset ->
        when (asset.type) {

            "طلا" -> {
                prices.gold18TomanPerGram?.let { livePrice ->
                    (asset.amount * livePrice).toLong()
                } ?: asset.currentValue
            }

            "تتر" -> {
                prices.usdtToman?.let { livePrice ->
                    (asset.amount * livePrice).toLong()
                } ?: asset.currentValue
            }

            else -> {
                asset.currentValue
            }
        }
    }

    val lastUpdateText = formatUpdateTime(prices.fetchedAt)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        /*
         * ارزش کل سرمایه
         */
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "ارزش کل سرمایه",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = Formatters.toman(totalCapital),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        /*
         * کادر قیمت‌های آنلاین
         */
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = "تتر",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "طلای ۱۸ عیار",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Column(
                            horizontalAlignment =
                                androidx.compose.ui.Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = prices.usdtToman?.let {
                                    Formatters.toman(it)
                                } ?: "—",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = prices.gold18TomanPerGram?.let {
                                    "${Formatters.toman(it)} / گرم"
                                } ?: "—",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        Button(
                            onClick = onRefresh,
                            enabled = !busy,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (busy) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .fillMaxWidth(0.15f),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("به‌روزرسانی")
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "آخرین به‌روزرسانی: $lastUpdateText",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        /*
         * دارایی لحظه‌ای
         */
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "دارایی لحظه‌ای",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFC62828)
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = Formatters.toman(liveCapital),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFC62828)
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "محاسبه‌شده با قیمت آنلاین طلا و تتر",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }

        /*
         * پیام زمانی که هنوز دارایی ثبت نشده است
         */
        if (assets.isEmpty()) {
            item {
                Text(
                    text = "هنوز دارایی ثبت نشده است. از بخش «دارایی‌ها» اولین مورد را اضافه کنید."
                )
            }
        }
    }
}

/*
 * تبدیل زمان آخرین بروزرسانی به تاریخ و ساعت شمسی
 *
 * فعلاً تاریخ میلادی نمایش داده می‌شود.
 * تبدیل کامل به تقویم شمسی را در مرحله بعد،
 * بدون دست زدن به منطق قیمت‌ها، اضافه می‌کنیم.
 */
private fun formatUpdateTime(timestamp: Long): String {
    if (timestamp <= 0L) {
        return "—"
    }

    return SimpleDateFormat(
        "yyyy/MM/dd - HH:mm",
        Locale("fa", "IR")
    ).format(Date(timestamp))
}
