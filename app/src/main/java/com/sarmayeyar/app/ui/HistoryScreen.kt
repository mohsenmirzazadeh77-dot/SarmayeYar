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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.HistoryPoint
import com.sarmayeyar.app.util.Formatters

@Composable
fun HistoryScreen(
    history: List<HistoryPoint>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "رشد سرمایه",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (history.size < 2) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme
                            .secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text =
                            "هنوز اطلاعات کافی برای رسم نمودار وجود ندارد.",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "حداقل دو ثبت ارزش سرمایه لازم است."
                    )
                }
            }

        } else {

            val min =
                history.minOf {
                    it.totalToman
                }.toFloat()

            val max =
                history.maxOf {
                    it.totalToman
                }.toFloat()

            val range =
                (max - min).coerceAtLeast(1f)

            val primaryColor =
                MaterialTheme.colorScheme.primary

            val surfaceColor =
                MaterialTheme.colorScheme.surfaceVariant

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {

                        /*
                         * خطوط راهنما
                         */
                        val guideColor =
                            Color.Gray.copy(alpha = 0.25f)

                        val guideCount = 4

                        for (i in 0..guideCount) {

                            val y =
                                size.height *
                                    i /
                                    guideCount

                            drawLine(
                                color = guideColor,
                                start =
                                    Offset(
                                        0f,
                                        y
                                    ),
                                end =
                                    Offset(
                                        size.width,
                                        y
                                    ),
                                strokeWidth = 1f
                            )
                        }

                        /*
                         * نقاط نمودار
                         */
                        val points =
                            history.mapIndexed { index, point ->

                                val x =
                                    size.width *
                                        index /
                                        (history.size - 1)

                                val y =
                                    size.height -
                                        (
                                            (
                                                point.totalToman
                                                    .toFloat() -
                                                    min
                                            ) / range
                                        ) *
                                        size.height

                                Offset(x, y)
                            }

                        /*
                         * خط نمودار
                         */
                        for (i in 1 until points.size) {

                            drawLine(
                                color = primaryColor,
                                start =
                                    points[i - 1],
                                end =
                                    points[i],
                                strokeWidth = 6f
                            )
                        }

                        /*
                         * نقاط روی نمودار
                         */
                        points.forEach { point ->

                            drawCircle(
                                color = primaryColor,
                                radius = 5f,
                                center = point
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val first =
                history.first().totalToman

            val last =
                history.last().totalToman

            val change =
                last - first

            val changePercent =
                if (first > 0) {
                    change * 100.0 / first
                } else {
                    0.0
                }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (change >= 0) {
                            Color(0xFFDDF4E4)
                        } else {
                            Color(0xFFFDE2E2)
                        }
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "وضعیت سرمایه",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "اولین ثبت: ${
                                Formatters.toman(first)
                            }"
                    )

                    Text(
                        text =
                            "آخرین ثبت: ${
                                Formatters.toman(last)
                            }"
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            if (change >= 0) {
                                "رشد سرمایه: ${
                                    Formatters.toman(change)
                                }"
                            } else {
                                "کاهش سرمایه: ${
                                    Formatters.toman(-change)
                                }"
                            },
                        style =
                            MaterialTheme.typography
                                .titleMedium
                    )

                    Text(
                        text =
                            "تغییر: ${
                                Formatters.percent(
                                    changePercent
                                )
                            }"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "تعداد ثبت‌ها: ${history.size}"
                )

                Text(
                    text =
                        "آخرین ارزش: ${
                            Formatters.toman(last)
                        }"
                )
            }
        }
    }
}
