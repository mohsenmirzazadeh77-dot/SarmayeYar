package com.sarmayeyar.app.repository

import com.sarmayeyar.app.data.LocalStore
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.model.HistoryPoint

class InvestmentRepository(private val store: LocalStore) {
    fun assets(): List<Asset> = store.loadAssets()
    fun history(): List<HistoryPoint> = store.loadHistory()

    fun saveAssets(assets: List<Asset>) = store.saveAssets(assets)
    fun saveHistory(history: List<HistoryPoint>) = store.saveHistory(history)

    fun backup(): String = store.exportJson()
    fun restore(json: String) = store.importJson(json)
}
