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

        // در نسخه جدید، افزودن دارایی Snapshot خودکار ایجاد نمی‌کند.
    }

    fun deleteAsset(asset: Asset) {
        val next = _assets.value.filterNot {
            it.id == asset.id
        }

        _assets.value = next
        repo.saveAssets(next)

        // حذف دارایی نیز Snapshot خودکار ایجاد نمی‌کند.
    }

    fun updateAsset(asset: Asset) {
        val next = _assets.value.map {
            if (it.id == asset.id) asset else it
        }

        _assets.value = next
        repo.saveAssets(next)

        // ویرایش دارایی نیز Snapshot خودکار ایجاد نمی‌کند.
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

            /*
             * نکته مهم:
             *
             * قیمت آنلاین دیگر داخل Asset ذخیره نمی‌شود.
             * بنابراین ارزش ثبت‌شده سرمایه تغییر نمی‌کند.
             *
             * همچنین Refresh دیگر Snapshot ایجاد نمی‌کند.
             */

            _busy.value = false
        }
    }

    /*
     * ارزش رسمی سرمایه:
     *
     * این مقدار همان ارزش ثبت‌شده دارایی‌های کاربر است
     * و به قیمت آنلاین وابسته نیست.
     */
    fun totalRegisteredValue(): Long {
        return _assets.value.sumOf {
            it.currentValue
        }
    }

    /*
     * ارزش لحظه‌ای:
     *
     * فعلاً برای طلا و تتر محاسبه می‌شود.
     * نقره و مس بعد از اضافه شدن منبع قیمت کاریزما
     * به این بخش اضافه خواهند شد.
     *
     * سایر دارایی‌ها همان ارزش ثبت‌شده خودشان را حفظ می‌کنند.
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
     * ثبت دستی Snapshot سود و زیان
     *
     * این تابع فقط زمانی باید فراخوانی شود که
     * کاربر خودش دستور محاسبه سود/زیان را بدهد.
     */
    fun recordProfitLossSnapshot() {

        val total = totalRegisteredValue()

        if (total <= 0L) {
            _message.value =
                "ارزش کل سرمایه برای ثبت سود/زیان معتبر نیست."
            return
        }

        val point = HistoryPoint(
            timestamp = System.currentTimeMillis(),
            totalToman = total
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
