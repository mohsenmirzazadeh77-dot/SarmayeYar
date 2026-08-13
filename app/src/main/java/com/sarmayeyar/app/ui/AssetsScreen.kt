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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    var showAdd by remember {
        mutableStateOf(false)
    }

    var editingAsset by remember {
        mutableStateOf<Asset?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "دارایی‌ها",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = {
                    showAdd = true
                }
            ) {
                Text("+ افزودن")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                items = assets,
                key = { it.id }
            ) { asset ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            assetCardColor(asset.type)
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = asset.name,
                                style =
                                    MaterialTheme.typography
                                        .titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    "${asset.type} • " +
                                    "${Formatters.number(asset.amount)} " +
                                    asset.unit
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    Formatters.toman(
                                        asset.currentValue
                                    ),
                                style =
                                    MaterialTheme.typography
                                        .titleMedium
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

            if (assets.isEmpty()) {
                item {
                    Text(
                        text =
                            "هنوز دارایی ثبت نشده است."
                    )
                }
            }
        }
    }

    /*
     * افزودن دارایی
     */
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

    /*
     * ویرایش دارایی
     */
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
        mutableStateOf(
            initialAsset?.name ?: ""
        )
    }

    var type by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.type ?: "نقد"
        )
    }

    var amount by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.amount?.toString() ?: "1"
        )
    }

    var buy by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.buyPriceToman?.toString()
                ?: ""
        )
    }

    var price by remember(initialAsset) {
        mutableStateOf(
            initialAsset?.currentPriceToman?.toString()
                ?: ""
        )
    }

    /*
     * عناوین اصلی دارایی
     */
    val types = listOf(
        "نقد",
        "طلا",
        "ارز",
        "بورس",
        "نقره",
        "تتر",
        "مس",
        "درآمد ثابت",
        "سایر"
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                /*
                 * عنوان فرعی
                 */
                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("عنوان فرعی")
                    },

                    placeholder = {
                        Text(
                            "مثلاً کاریزما"
                        )
                    }
                )

                Text(
                    text = "عنوان اصلی",
                    style =
                        MaterialTheme.typography
                            .titleSmall
                )

                /*
                 * انتخاب عنوان اصلی
                 */
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    types.chunked(3).forEach { rowTypes ->

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {

                            rowTypes.forEach { item ->

                                FilterChip(
                                    selected =
                                        type == item,

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

                /*
                 * مقدار دارایی
                 */
                OutlinedTextField(
                    value = amount,

                    onValueChange = {
                        amount = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("تعداد / مقدار")
                    }
                )

                /*
                 * قیمت خرید
                 */
                OutlinedTextField(
                    value = buy,

                    onValueChange = {
                        buy = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "قیمت خرید هر واحد (تومان)"
                        )
                    }
                )

                /*
                 * ارزش فعلی ثبت‌شده
                 *
                 * این مقدار برای سرمایه رسمی
                 * مورد استفاده قرار می‌گیرد.
                 */
                OutlinedTextField(
                    value = price,

                    onValueChange = {
                        price = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "ارزش فعلی ثبت‌شده (تومان)"
                        )
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
                            .replace("٬", "")
                            .toDoubleOrNull()
                            ?: 0.0

                    val parsedBuy =
                        buy
                            .replace(",", "")
                            .replace("٬", "")
                            .toLongOrNull()
                            ?: 0L

                    val parsedPrice =
                        price
                            .replace(",", "")
                            .replace("٬", "")
                            .toLongOrNull()
                            ?: 0L

                    if (
                        name.isNotBlank() &&
                        parsedAmount > 0.0 &&
                        parsedPrice > 0L
                    ) {

                        /*
                         * فقط تتر فعلاً آنلاین است.
                         *
                         * طلا، نقره و مس فعلاً
                         * ارزش ثبت‌شده کاربر را حفظ می‌کنند.
                         */
                        val livePrice =
                            type == "تتر"

                        val unit =
                            when (type) {

                                "طلا",
                                "نقره" -> "گرم"

                                else -> "واحد"
                            }

                        onSave(
                            Asset(
                                id =
                                    initialAsset?.id
                                        ?: 0L,

                                name =
                                    name.trim(),

                                type = type,

                                amount =
                                    parsedAmount,

                                unit = unit,

                                buyPriceToman =
                                    parsedBuy,

                                currentPriceToman =
                                    parsedPrice,

                                isLivePrice =
                                    livePrice,

                                createdAt =
                                    initialAsset
                                        ?.createdAt
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

/*
 * رنگ پس‌زمینه کادر دارایی‌ها
 *
 * این رنگ‌ها بعداً در کل برنامه
 * به عنوان استاندارد رنگ دارایی‌ها استفاده می‌شوند.
 */
private fun assetCardColor(
    type: String
): Color {

    return when (type) {

        // طلا
        "طلا" ->
            Color(0xFFFFF3CD)

        // نقره
        "نقره" ->
            Color(0xFFE5E7EB)

        // مس
        "مس" ->
            Color(0xFFF1D0BD)

        // درآمد ثابت
        "درآمد ثابت" ->
            Color(0xFFDDF4E4)

        // سهام / بورس
        "بورس" ->
            Color(0xFFDCEBFF)

        // تتر
        "تتر" ->
            Color(0xFFE9D5FF)

        // سایر
        "سایر" ->
            Color.White

        // نقد
        "نقد" ->
            Color(0xFFF3F4F6)

        // ارز
        "ارز" ->
            Color(0xFFE8EEF5)

        else ->
            Color(0xFFF5F5F5)
    }
}
