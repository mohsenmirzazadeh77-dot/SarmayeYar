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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters

@Composable
fun AnalysisScreen(
    assets: List<Asset>
) {
    val total = assets.sumOf { it.currentValue }

    /*
     * گروه‌بندی دارایی‌ها بر اساس عنوان اصلی
     */
    val groups = assets
        .groupBy { it.type }
        .map { (type, items) ->
            val value = items.sumOf { it.currentValue }

            AnalysisGroup(
                type = type,
                value = value,
                count = items.size
            )
        }
        .sortedByDescending { it.value }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {
            Text(
                text = "تحلیل سرمایه",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "تفکیک سرمایه بر اساس نوع دارایی",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "ارزش کل سرمایه",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = Formatters.toman(total),
                        style =
                            MaterialTheme.typography.headlineSmall
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "تعداد گروه‌ها: ${groups.size}"
                    )
                }
            }
        }

        if (groups.isEmpty()) {

            item {
                Text(
                    text =
                        "برای تحلیل، ابتدا دارایی ثبت کنید."
                )
            }

        } else {

            items(
                items = groups,
                key = { it.type }
            ) { group ->

                val share =
                    if (total > 0) {
                        group.value * 100.0 / total
                    } else {
                        0.0
                    }

                AnalysisCard(
                    group = group,
                    share = share
                )
            }
        }
    }
}

private data class AnalysisGroup(
    val type: String,
    val value: Long,
    val count: Int
)

@Composable
private fun AnalysisCard(
    group: AnalysisGroup,
    share: Double
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                analysisCardColor(group.type)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = group.type,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                        Formatters.percent(share),
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    Formatters.toman(group.value),
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "${group.count} دارایی ثبت‌شده"
            )
        }
    }
}

private fun analysisCardColor(
    type: String
): Color {

    return when (type) {

        "طلا" ->
            Color(0xFFFFF3CD)

        "نقره" ->
            Color(0xFFE5E7EB)

        "مس" ->
            Color(0xFFF1D0BD)

        "تتر" ->
            Color(0xFFE9D5FF)

        "بورس" ->
            Color(0xFFDCEBFF)

        "درآمد ثابت" ->
            Color(0xFFDDF4E4)

        "نقد" ->
            Color(0xFFF3F4F6)

        "ارز" ->
            Color(0xFFE8EEF5)

        "سایر" ->
            Color.White

        else ->
            Color(0xFFF5F5F5)
    }
}
