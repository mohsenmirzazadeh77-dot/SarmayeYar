package com.sarmayeyar.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.model.HistoryPoint
import com.sarmayeyar.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfitLossScreen(
    assets: List<Asset>,
    history: List<HistoryPoint>,
    onRecord: () -> Unit
) {
    val total =
        assets.sumOf {
            it.currentValue
        }

    val previous =
        history.lastOrNull()

    val difference =
        if (previous != null) {
            total - previous.totalToman
        } else {
            0L
        }

    val percent =
        if (
            previous != null &&
            previous.totalToman > 0L
        ) {
            difference * 100.0 /
                    previous.totalToman
        } else {
            0.0
        }

    val differenceColor =
        when {
            difference > 0L ->
                Color(0xFF16803C)

            difference < 0L ->
                Color(0xFFC62828)

            else ->
                MaterialTheme.colorScheme.onSurface
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                "سود/زیان",
                style =
                    MaterialTheme.typography.headlineMedium
            )
        }

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "ثبت محاسبه سود/زیان",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        "ثبت سود/زیان فقط با دستور شما انجام می‌شود."
                    )

                    Button(
                        onClick = onRecord,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "ثبت محاسبه سود/زیان"
                        )
                    }
                }
            }
        }

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "سود/زیان کلی",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        "ارزش فعلی سرمایه"
                    )

                    Text(
                        Formatters.toman(total),
                        style =
                            MaterialTheme.typography.headlineSmall
                    )

                    if (previous == null) {

                        Text(
                            "هنوز نقطه مبنایی برای مقایسه ثبت نشده است."
                        )

                    } else {

                        Text(
                            "آخرین ثبت: ${
                                Formatters.toman(
                                    previous.totalToman
                                )
                            }"
                        )

                        Text(
                            if (difference >= 0L) {
                                "سود: ${
                                    Formatters.toman(
                                        difference
                                    )
                                }"
                            } else {
                                "زیان: ${
                                    Formatters.toman(
                                        -difference
                                    )
                                }"
                            },
                            color =
                                differenceColor,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "درصد تغییر: ${
                                Formatters.percent(
                                    percent
                                )
                            }",
                            color =
                                differenceColor
                        )
                    }
                }
            }
        }

        item {

            Text(
                "سود/زیان جزئی‌نگر",
                style =
                    MaterialTheme.typography.titleLarge
            )
        }

        if (assets.isEmpty()) {

            item {
                Text(
                    "هنوز دارایی ثبت نشده است."
                )
            }

        } else {

            items(
                items = assets,
                key = {
                    it.id
                }
            ) { asset ->

                val previousValue =
                    history
                        .lastOrNull()
                        ?.categoryValues
                        ?.get(asset.name)

                val currentValue =
                    asset.currentValue

                val assetDifference =
                    if (previousValue != null) {
                        currentValue -
                                previousValue
                    } else {
                        0L
                    }

                val assetPercent =
                    if (
                        previousValue != null &&
                        previousValue > 0L
                    ) {
                        assetDifference *
                                100.0 /
                                previousValue
                    } else {
                        0.0
                    }

                val assetColor =
                    when {
                        assetDifference > 0L ->
                            Color(0xFF16803C)

                        assetDifference < 0L ->
                            Color(0xFFC62828)

                        else ->
                            MaterialTheme.colorScheme.onSurface
                    }

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            asset.name,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "ارزش فعلی: ${
                                Formatters.toman(
                                    currentValue
                                )
                            }"
                        )

                        if (previousValue == null) {

                            Text(
                                "برای این دارایی هنوز نقطه مبنا ثبت نشده است."
                            )

                        } else {

                            Text(
                                "ارزش قبلی: ${
                                    Formatters.toman(
                                        previousValue
                                    )
                                }"
                            )

                            Text(
                                if (
                                    assetDifference >= 0L
                                ) {
                                    "سود: ${
                                        Formatters.toman(
                                            assetDifference
                                        )
                                    }"
                                } else {
                                    "زیان: ${
                                        Formatters.toman(
                                            -assetDifference
                                        )
                                    }"
                                },
                                color =
                                    assetColor
                            )

                            Text(
                                "درصد تغییر: ${
                                    Formatters.percent(
                                        assetPercent
                                    )
                                }",
                                color =
                                    assetColor
                            )
                        }
                    }
                }
            }
        }

        item {

            Text(
                "خط زمان ارزش سرمایه",
                style =
                    MaterialTheme.typography.titleLarge
            )
        }

        item {

            HistoryChart(history)
        }

        if (history.isNotEmpty()) {

            item {

                Text(
                    "تعداد ثبت‌ها: ${history.size}"
                )
            }
        }
    }
}

@Composable
private fun HistoryChart(
    history: List<HistoryPoint>
) {

    if (history.size < 2) {

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                "برای نمایش نمودار حداقل دو ثبت سود/زیان لازم است.",
                modifier =
                    Modifier.padding(16.dp)
            )
        }

        return
    }

    val min =
        history.minOf {
            it.totalToman
        }.toFloat()

    val max =
        history.maxOf {
            it.totalToman
        }.toFloat()

    val range =
        (max - min)
            .coerceAtLeast(1f)

    val primary =
        MaterialTheme.colorScheme.primary

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
            ) {

                val points =
                    history.mapIndexed { index, point ->

                        val x =
                            size.width *
                                    index /
                                    (history.size - 1)

                        val normalized =
                            (
                                point.totalToman
                                    .toFloat() - min
                            ) / range

                        val y =
                            size.height *
                                    (1f - normalized)

                        Offset(x, y)
                    }

                for (
                    i in 1 until points.size
                ) {

                    drawLine(
                        color = primary,
                        start =
                            points[i - 1],
                        end =
                            points[i],
                        strokeWidth = 5f
                    )
                }
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    formatDate(
                        history.first().timestamp
                    )
                )

                Text(
                    formatDate(
                        history.last().timestamp
                    )
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                "آخرین ارزش ثبت‌شده: ${
                    Formatters.toman(
                        history.last().totalToman
                    )
                }"
            )
        }
    }
}

private fun formatDate(
    timestamp: Long
): String {

    return SimpleDateFormat(
        "yyyy/MM/dd",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )
}
