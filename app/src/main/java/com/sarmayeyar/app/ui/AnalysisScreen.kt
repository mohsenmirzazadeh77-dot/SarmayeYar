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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters
import kotlin.math.PI

@Composable
fun AnalysisScreen(
    assets: List<Asset>
) {

    val total =
        assets.sumOf {
            it.currentValue
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Text(
            "تحلیل سرمایه",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            Modifier.height(12.dp)
        )

        if (assets.isEmpty()) {

            Text(
                "برای تحلیل، ابتدا دارایی ثبت کنید."
            )

            return
        }

        /*
         * =====================================================
         * نمودار دایره‌ای ترکیب سبد
         * =====================================================
         */

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {

                Text(
                    "ترکیب سبد سرمایه",
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                PortfolioPieChart(
                    assets = assets,
                    total = total
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(
                    "ارزش کل سرمایه: ${
                        Formatters.toman(total)
                    }"
                )
            }
        }

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "جزئیات دارایی‌ها",
            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            Modifier.height(8.dp)
        )

        assets.forEach { asset ->

            val share =
                if (total > 0L) {
                    asset.currentValue *
                            100.0 /
                            total
                } else {
                    0.0
                }

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 4.dp
                        )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            asset.name,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            asset.type
                        )
                    }

                    Column {

                        Text(
                            Formatters.toman(
                                asset.currentValue
                            )
                        )

                        Text(
                            "سهم: ${
                                Formatters.percent(
                                    share
                                )
                            }"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioPieChart(
    assets: List<Asset>,
    total: Long
) {

    val palette =
        listOf(
            Color(0xFFD4AF37),
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFF9C27B0),
            Color(0xFFFF9800),
            Color(0xFF009688),
            Color(0xFF795548),
            Color(0xFF607D8B),
            Color(0xFFE91E63)
        )

    val slices =
        assets.mapIndexed { index, asset ->

            val share =
                if (total > 0L) {
                    asset.currentValue.toFloat() /
                            total.toFloat()
                } else {
                    0f
                }

            PieSlice(
                name = asset.name,
                share = share,
                color =
                    palette[
                        index % palette.size
                    ]
            )
        }

    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(260.dp)
    ) {

        val diameter =
            minOf(
                size.width,
                size.height
            ) * 0.72f

        val topLeft =
            Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )

        var startAngle =
            -90f

        slices.forEach { slice ->

            val sweep =
                slice.share * 360f

            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = topLeft,
                size =
                    androidx.compose.ui.geometry.Size(
                        diameter,
                        diameter
                    )
            )

            startAngle += sweep
        }

        /*
         * حاشیه دایره برای ظاهر مرتب‌تر
         */
        drawArc(
            color =
                MaterialTheme.colorScheme
                    .onSurface
                    .copy(alpha = 0.15f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size =
                androidx.compose.ui.geometry.Size(
                    diameter,
                    diameter
                ),
            style =
                Stroke(width = 2f)
        )
    }

    Spacer(
        Modifier.height(8.dp)
    )

    /*
     * راهنمای رنگ‌ها
     */
    Column(
        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {

        slices.forEach { slice ->

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Row {

                    Canvas(
                        modifier =
                            Modifier
                                .padding(end = 8.dp)
                                .height(14.dp)
                                .fillMaxWidth(0.04f)
                    ) {

                        drawRect(
                            color = slice.color
                        )
                    }

                    Text(
                        slice.name
                    )
                }

                Text(
                    Formatters.percent(
                        slice.share * 100.0
                    )
                )
            }
        }
    }
}

private data class PieSlice(
    val name: String,
    val share: Float,
    val color: Color
)
