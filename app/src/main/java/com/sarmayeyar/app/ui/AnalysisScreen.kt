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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters
import kotlin.math.min

@Composable
fun AnalysisScreen(
    assets: List<Asset>
) {

    val total =
        assets.sumOf {
            it.currentValue
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        item {

            Text(
                "تحلیل سرمایه",
                style =
                    MaterialTheme.typography
                        .headlineMedium
            )
        }

        if (assets.isNotEmpty()) {

            item {

                PortfolioPieChart(
                    assets = assets
                )
            }
        }

        items(
            items = assets,
            key = { it.id }
        ) { asset ->

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
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        asset.name,
                        style =
                            MaterialTheme.typography
                                .titleMedium
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        "نوع: ${asset.type}"
                    )

                    Text(
                        "سهم فعلی: " +
                                Formatters.percent(
                                    share
                                )
                    )

                    Text(
                        "ارزش: " +
                                Formatters.toman(
                                    asset.currentValue
                                )
                    )

                    Text(
                        "سرمایه‌گذاری اولیه: " +
                                Formatters.toman(
                                    asset.investedValue
                                )
                    )

                    Text(
                        "سود/زیان: " +
                                Formatters.toman(
                                    asset.profit
                                )
                    )
                }
            }
        }

        if (assets.isEmpty()) {

            item {

                Text(
                    "برای تحلیل، ابتدا دارایی ثبت کنید."
                )
            }
        }
    }
}


@Composable
private fun PortfolioPieChart(
    assets: List<Asset>
) {

    val total =
        assets.sumOf {
            it.currentValue
        }

    if (total <= 0L) {
        return
    }

    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error
        )

    val outlineColor =
        MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.15f
        )

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            "ترکیب سبد سرمایه",
            style =
                MaterialTheme.typography
                    .titleLarge
        )

        Spacer(
            Modifier.height(10.dp)
        )

        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
        ) {

            val diameter =
                min(
                    size.width,
                    size.height
                ) * 0.75f

            val left =
                (size.width - diameter) / 2f

            val top =
                (size.height - diameter) / 2f

            var startAngle =
                -90f

            assets.forEachIndexed { index, asset ->

                val sweepAngle =
                    asset.currentValue
                        .toFloat() /
                            total.toFloat() *
                            360f

                drawArc(
                    color =
                        colors[
                            index %
                                colors.size
                        ],

                    startAngle =
                        startAngle,

                    sweepAngle =
                        sweepAngle,

                    useCenter = true,

                    topLeft =
                        Offset(
                            left,
                            top
                        ),

                    size =
                        Size(
                            diameter,
                            diameter
                        )
                )

                startAngle +=
                    sweepAngle
            }

            drawArc(
                color = outlineColor,

                startAngle = 0f,

                sweepAngle = 360f,

                useCenter = false,

                topLeft =
                    Offset(
                        left,
                        top
                    ),

                size =
                    Size(
                        diameter,
                        diameter
                    ),

                style =
                    Stroke(
                        width = 2f
                    )
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        assets.forEachIndexed { index, asset ->

            val share =
                asset.currentValue *
                        100.0 /
                        total

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
                                .padding(
                                    end = 8.dp
                                )
                                .size(12.dp)
                    ) {

                        drawCircle(
                            color =
                                colors[
                                    index %
                                        colors.size
                                ]
                        )
                    }

                    Text(
                        asset.name
                    )
                }

                Text(
                    Formatters.percent(
                        share
                    )
                )
            }
        }
    }
}
