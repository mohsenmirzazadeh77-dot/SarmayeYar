package com.sarmayeyar.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.util.Formatters

@Composable
fun AssetsScreen(
    assets: List<Asset>,
    onAdd: (Asset) -> Unit,
    onDelete: (Asset) -> Unit,
    onUpdate: (Asset) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var editingAsset by remember { mutableStateOf<Asset?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "دارایی‌ها",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = { showAdd = true }
            ) {
                Text("+ افزودن")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = assets,
                key = { it.id }
            ) { asset ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                asset.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "${asset.type} • " +
                                    "${Formatters.number(asset.amount)} " +
                                    asset.unit
                            )

                            Text(
                                Formatters.toman(asset.currentValue),
                                color = assetColor(asset.type),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Row {
                            TextButton(
                                onClick = {
                                    editingAsset = asset
                                }
                            ) {
                                Text("ویرایش")
                            }

                            TextButton(
                                onClick = {
                                    onDelete(asset)
                                }
                            ) {
                                Text("حذف")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AssetDialog(
            title = "افزودن دارایی",
            initialAsset = null,
            onDismiss = {
                showAdd = false
            },
            onSave = {
                onAdd(it)
                showAdd = false
            }
        )
    }

    editingAsset?.let { asset ->
        AssetDialog(
            title = "ویرایش دارایی",
            initialAsset = asset,
            onDismiss = {
                editingAsset = null
            },
            onSave = {
                onUpdate(it)
                editingAsset = null
            }
        )
    }
}

@Composable
private fun AssetDialog(
    title: String,
    initialAsset: Asset?,
    onDismiss: () -> Unit,
    onSave: (Asset) -> Unit
) {
    var name by remember(initialAsset) {
        mutableStateOf(initialAsset?.name ?: "")
    }

    var type by remember(initialAsset) {
        mutableStateOf(initialAsset?.type ?: "نقد")
    }

    var amount by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.amount?.toString() ?: "1"
        )
    }

    var buy by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.buyPriceToman?.toString() ?: ""
        )
    }

    var price by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.currentPriceToman?.toString() ?: ""
        )
    }

    val types = listOf(
        "نقد",
        "طلا",
        "ارز",
        "بورس",
        "نقره",
        "تتر",
        "مس",
        "سایر"
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("عنوان فرعی")
                    },
                    placeholder = {
                        Text("مثلاً کاریزما")
                    }
                )

                Text(
                    "عنوان اصلی",
                    style = MaterialTheme.typography.titleSmall
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    types.chunked(4).forEach { rowTypes ->
                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {
                            rowTypes.forEach { item ->
                                FilterChip(
                                    selected = type == item,
                                    onClick = {
                                        type = item
                                    },
                                    label = {
                                        Text(item)
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("تعداد / مقدار")
                    }
                )

                OutlinedTextField(
                    value = buy,
                    onValueChange = {
                        buy = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("قیمت خرید هر واحد (تومان)")
                    }
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("ارزش فعلی ثبت‌شده (تومان)")
                    }
                )
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount =
                        amount
                            .replace(",", "")
                            .toDoubleOrNull()
                            ?: 0.0

                    val parsedBuy =
                        buy
                            .replace(",", "")
                            .toLongOrNull()
                            ?: 0L

                    val parsedPrice =
                        price
                            .replace(",", "")
                            .toLongOrNull()
                            ?: 0L

                    if (
                        name.isNotBlank() &&
                        parsedAmount > 0.0 &&
                        parsedPrice > 0L
                    ) {
                        onSave(
                            Asset(
                                id = initialAsset?.id ?: 0L,
                                name = name.trim(),
                                type = type,
                                amount = parsedAmount,
                                unit =
                                    if (
                                        type == "طلا" ||
                                        type == "نقره"
                                    ) {
                                        "گرم"
                                    } else {
                                        "واحد"
                                    },
                                buyPriceToman = parsedBuy,
                                currentPriceToman = parsedPrice,
                                isLivePrice =
                                    type == "طلا" ||
                                    type == "نقره" ||
                                    type == "تتر" ||
                                    type == "مس",
                                createdAt =
                                    initialAsset?.createdAt
                                        ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
            ) {
                Text("ذخیره")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("انصراف")
            }
        }
    )
}

private fun assetColor(type: String): Color {
    return when (type) {
        "طلا" -> Color(0xFFFFC107)
        "نقره" -> Color(0xFFB0BEC5)
        "مس" -> Color(0xFFB87333)
        "تتر" -> Color(0xFF7E57C2)
        "بورس" -> Color(0xFF42A5F5)
        "درآمد ثابت" -> Color(0xFF66BB6A)
        "سایر" -> Color.White
        else -> Color.Unspecified
    }
}
