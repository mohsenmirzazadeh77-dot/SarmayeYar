package com.sarmayeyar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarmayeyar.app.data.LocalStore
import com.sarmayeyar.app.model.Asset
import com.sarmayeyar.app.model.HistoryPoint
import com.sarmayeyar.app.network.LivePrices
import com.sarmayeyar.app.network.PriceService
import com.sarmayeyar.app.repository.InvestmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = InvestmentRepository(LocalStore(app))
    private val priceService = PriceService()

    private val _assets = MutableStateFlow(repo.assets())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    private val _history = MutableStateFlow(repo.history())
    val history: StateFlow<List<HistoryPoint>> = _history.asStateFlow()

    private val _prices = MutableStateFlow(LivePrices())
    val prices: StateFlow<LivePrices> = _prices.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addAsset(asset: Asset) {
        val next = _assets.value + asset.copy(
            id = System.currentTimeMillis()
        )

        _assets.value = next
        repo.saveAssets(next)
    }

    fun deleteAsset(asset: Asset) {
        val next = _assets.value.filterNot {
            it.id == asset.id
        }

        _assets.value = next
        repo.saveAssets(next)
    }

    fun updateAsset(asset: Asset) {
        val next = _assets.value.map {
            if (it.id == asset.id) asset else it
        }

        _assets.value = next
        repo.saveAssets(next)
    }

    fun refreshPrices() {
        viewModelScope.launch {

            _busy.value = true

            val result = withContext(Dispatchers.IO) {
                priceService.fetch()
            }

            _prices.value = result

            if (
                result.usdtToman == null &&
                result.gold18TomanPerGram == null
            ) {
                _message.value =
                    "دریافت قیمت آنلاین ناموفق بود."
            } else {
                _message.value =
                    "قیمت‌های آنلاین به‌روزرسانی شدند."
            }

            _busy.value = false
        }
    }

    /*
     * ارزش رسمی سرمایه.
     *
     * این مقدار فقط از ارزش ثبت‌شده دارایی‌ها استفاده می‌کند
     * و به قیمت آنلاین وابسته نیست.
     */
    fun totalRegisteredValue(): Long {
        return _assets.value.sumOf {
            it.currentValue
        }
    }

    /*
     * ارزش لحظه‌ای سرمایه.
     *
     * این مقدار صرفاً برای نمایش وضعیت لحظه‌ای کاربر است
     * و نباید جایگزین ارزش رسمی سرمایه شود.
     */
    fun totalLiveValue(): Long {

        return _assets.value.sumOf { asset ->

            when (asset.type) {

                "تتر" -> {
                    val price = _prices.value.usdtToman

                    if (price != null) {
                        (asset.amount * price).toLong()
                    } else {
                        asset.currentValue
                    }
                }

                "طلا" -> {
                    val price =
                        _prices.value.gold18TomanPerGram

                    if (price != null) {
                        (asset.amount * price).toLong()
                    } else {
                        asset.currentValue
                    }
                }

                else -> {
                    asset.currentValue
                }
            }
        }
    }

    /*
     * ثبت دستی Snapshot سود و زیان.
     *
     * این تابع فقط با دستور مستقیم کاربر اجرا می‌شود.
     */
    fun recordProfitLossSnapshot() {

        val total = totalRegisteredValue()

        if (total <= 0L) {
            _message.value =
                "ارزش کل سرمایه برای ثبت سود/زیان معتبر نیست."
            return
        }

        /*
         * ارزش هر عنوان دارایی در زمان ثبت.
         *
         * این مقادیر از ارزش رسمی ثبت‌شده دارایی‌ها
         * گرفته می‌شوند و قیمت آنلاین در آنها دخالت ندارد.
         */
        val categoryValues =
            _assets.value
                .groupBy { it.type }
                .mapValues { (_, categoryAssets) ->
                    categoryAssets.sumOf { it.currentValue }
                }

        val point = HistoryPoint(
            timestamp = System.currentTimeMillis(),
            totalToman = total,
            categoryValues = categoryValues
        )

        val nextHistory =
            (_history.value + point).takeLast(365)

        _history.value = nextHistory
        repo.saveHistory(nextHistory)

        _message.value =
            "ثبت سود/زیان با موفقیت انجام شد."
    }

    fun clearMessage() {
        _message.value = null
    }

    fun backup(): String {
        return repo.backup()
    }

    fun restore(json: String) {
        repo.restore(json)

        _assets.value = repo.assets()
        _history.value = repo.history()
    }
}
