package com.sarmayeyar.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.HistoryPoint
import com.sarmayeyar.app.util.Formatters

@Composable
fun HistoryScreen(history: List<HistoryPoint>) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "رشد سرمایه",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (history.size < 2) {
            Text("برای نمایش نمودار، حداقل دو ثبت ارزش سرمایه لازم است.")
        } else {
            val min = history.minOf { it.totalToman }.toFloat()
            val max = history.maxOf { it.totalToman }.toFloat()

            val primaryColor = MaterialTheme.colorScheme.primary

            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val range = (max - min).coerceAtLeast(1f)

                val pts = history.mapIndexed { i, p ->
                    val x = size.width * i / (history.size - 1)

                    val y =
                        size.height -
                            ((p.totalToman - min) / range) * size.height

                    Offset(x, y)
                }

                for (i in 1 until pts.size) {
                    drawLine(
                        color = primaryColor,
                        start = pts[i - 1],
                        end = pts[i],
                        strokeWidth = 5f
                    )
                }
            }

            Text(
                "آخرین ارزش: ${Formatters.toman(history.last().totalToman)}"
            )
        }
    }
}
