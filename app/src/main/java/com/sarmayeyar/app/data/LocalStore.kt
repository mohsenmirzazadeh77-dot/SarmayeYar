package com.sarmayeyar.app.data

import android.content.Context
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.model.HistoryPoint
import org.json.JSONArray
import org.json.JSONObject

class LocalStore(context: Context) {

    private val prefs =
        context.getSharedPreferences("sarmayeyar", Context.MODE_PRIVATE)

    fun loadAssets(): List<Asset> {
        val raw = prefs.getString("assets", "[]") ?: "[]"
        val arr = JSONArray(raw)

        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)

                add(
                    Asset(
                        id = o.optLong("id"),
                        name = o.optString("name"),
                        type = o.optString("type"),
                        amount = o.optDouble("amount", 1.0),
                        unit = o.optString("unit", "عدد"),
                        buyPriceToman = o.optLong("buyPriceToman"),
                        currentPriceToman = o.optLong("currentPriceToman"),
                        isLivePrice = o.optBoolean("isLivePrice"),
                        createdAt = o.optLong("createdAt")
                    )
                )
            }
        }
    }

    fun saveAssets(assets: List<Asset>) {
        val arr = JSONArray()

        assets.forEach { a ->
            arr.put(
                JSONObject().apply {
                    put("id", a.id)
                    put("name", a.name)
                    put("type", a.type)
                    put("amount", a.amount)
                    put("unit", a.unit)
                    put("buyPriceToman", a.buyPriceToman)
                    put("currentPriceToman", a.currentPriceToman)
                    put("isLivePrice", a.isLivePrice)
                    put("createdAt", a.createdAt)
                }
            )
        }

        prefs.edit()
            .putString("assets", arr.toString())
            .apply()
    }

    fun loadHistory(): List<HistoryPoint> {
        val raw = prefs.getString("history", "[]") ?: "[]"
        val arr = JSONArray(raw)

        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)

                val categoryValues = mutableMapOf<String, Long>()

                val categories =
                    o.optJSONObject("categoryValues")

                if (categories != null) {
                    val keys = categories.keys()

                    while (keys.hasNext()) {
                        val key = keys.next()
                        categoryValues[key] =
                            categories.optLong(key)
                    }
                }

                add(
                    HistoryPoint(
                        timestamp = o.optLong("timestamp"),
                        totalToman = o.optLong("totalToman"),
                        categoryValues = categoryValues
                    )
                )
            }
        }
    }

    fun saveHistory(history: List<HistoryPoint>) {
        val arr = JSONArray()

        history.takeLast(365).forEach { point ->

            val categories = JSONObject()

            point.categoryValues.forEach { (key, value) ->
                categories.put(key, value)
            }

            arr.put(
                JSONObject().apply {
                    put("timestamp", point.timestamp)
                    put("totalToman", point.totalToman)
                    put("categoryValues", categories)
                }
            )
        }

        prefs.edit()
            .putString("history", arr.toString())
            .apply()
    }

    fun exportJson(): String {
        val root = JSONObject()

        root.put("version", 2)
        root.put(
            "exportedAt",
            System.currentTimeMillis()
        )

        root.put(
            "assets",
            prefs.getString("assets", "[]")
        )

        root.put(
            "history",
            prefs.getString("history", "[]")
        )

        return root.toString(2)
    }

    fun importJson(json: String) {
        val root = JSONObject(json)

        prefs.edit()
            .putString(
                "assets",
                root.optString("assets", "[]")
            )
            .putString(
                "history",
                root.optString("history", "[]")
            )
            .apply()
    }
}
