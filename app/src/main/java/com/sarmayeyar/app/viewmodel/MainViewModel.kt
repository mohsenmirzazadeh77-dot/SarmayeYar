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

    private val repo =
        InvestmentRepository(LocalStore(app))

    private val priceService =
        PriceService()

    private val _assets =
        MutableStateFlow(repo.assets())

    val assets: StateFlow<List<Asset>> =
        _assets.asStateFlow()

    private val _history =
        MutableStateFlow(repo.history())

    val history: StateFlow<List<HistoryPoint>> =
        _history.asStateFlow()

    private val _prices =
        MutableStateFlow(LivePrices())

    val prices: StateFlow<LivePrices> =
        _prices.asStateFlow()

    private val _busy =
        MutableStateFlow(false)

    val busy: StateFlow<Boolean> =
        _busy.asStateFlow()

    private val _message =
        MutableStateFlow<String?>(null)

    val message: StateFlow<String?> =
        _message.asStateFlow()

    /*
     * افزودن دارایی
     *
     * توجه:
     * این عملیات دیگر Snapshot سود/زیان ایجاد نمی‌کند.
     */
    fun addAsset(asset: Asset) {

        val next =
            _assets.value +
                    asset.copy(
                        id = System.currentTimeMillis()
                    )

        _assets.value = next

        repo.saveAssets(next)
    }

    /*
     * حذف دارایی
     *
     * Snapshot خودکار ایجاد نمی‌شود.
     */
    fun deleteAsset(asset: Asset) {

        val next =
            _assets.value.filterNot {
                it.id == asset.id
            }

        _assets.value = next

        repo.saveAssets(next)
    }

    /*
     * ویرایش دارایی
     *
     * Snapshot خودکار ایجاد نمی‌شود.
     */
    fun updateAsset(asset: Asset) {

        val next =
            _assets.value.map {
                if (it.id == asset.id) {
                    asset
                } else {
                    it
                }
            }

        _assets.value = next

        repo.saveAssets(next)
    }

    /*
     * دریافت قیمت آنلاین
     *
     * قیمت آنلاین فقط برای به‌روزرسانی
     * ارزش لحظه‌ای دارایی‌ها استفاده می‌شود.
     *
     * این عملیات Snapshot سود/زیان ایجاد نمی‌کند.
     */
    fun refreshPrices() {

        viewModelScope.launch {

            _busy.value = true

            _message.value =
                "در حال دریافت قیمت تتر و طلا..."

            val result =
                withContext(Dispatchers.IO) {
                    priceService.fetch()
                }

            _prices.value = result

            val usdtOk =
                result.usdtToman != null &&
                        result.usdtToman > 0

            /*
             * قیمت آنلاین طلا فعلاً در محاسبات آنلاین
             * برنامه نگه داشته می‌شود، اما در صورت
             * نبود قیمت، هیچ خطایی ایجاد نمی‌کنیم.
             */
            val goldOk =
                result.gold18TomanPerGram != null &&
                        result.gold18TomanPerGram > 0

            _message.value =
                when {

                    usdtOk && goldOk -> {
                        "تتر و طلای ۱۸ عیار با موفقیت به‌روزرسانی شدند."
                    }

                    usdtOk && !goldOk -> {
                        "تتر با موفقیت دریافت شد؛ " +
                                "اما قیمت طلای ۱۸ عیار دریافت نشد."
                    }

                    !usdtOk && goldOk -> {
                        "قیمت طلا دریافت شد؛ " +
                                "اما تتر دریافت نشد."
                    }

                    else -> {
                        "دریافت قیمت تتر و طلا ناموفق بود."
                    }
                }

            applyLivePrices(result)

            _busy.value = false
        }
    }

    /*
     * اعمال قیمت‌های آنلاین روی دارایی‌ها
     *
     * این تابع Snapshot ایجاد نمی‌کند.
     */
    private fun applyLivePrices(
        p: LivePrices
    ) {

        val next =
            _assets.value.map { asset ->

                when (asset.type) {

                    "تتر" -> {

                        p.usdtToman?.let { price ->

                            asset.copy(
                                currentPriceToman = price,
                                isLivePrice = true
                            )

                        } ?: asset
                    }

                    /*
                     * قیمت آنلاین طلا فعلاً فقط در صورت
                     * دریافت موفق اعمال می‌شود.
                     *
                     * اگر قیمت طلا دریافت نشود،
                     * مقدار قبلی دارایی دست‌نخورده می‌ماند.
                     */
                    "طلا" -> {

                        p.gold18TomanPerGram?.let { price ->

                            asset.copy(
                                currentPriceToman = price,
                                isLivePrice = true
                            )

                        } ?: asset
                    }

                    else -> asset
                }
            }

        _assets.value = next

        repo.saveAssets(next)
    }

    /*
     * =========================================================
     * ثبت دستی Snapshot سود/زیان
     * =========================================================
     *
     * این تنها نقطه‌ای است که HistoryPoint جدید ایجاد می‌شود.
     *
     * کاربر باید خودش از صفحه «سود/زیان»
     * دستور ثبت محاسبه را صادر کند.
     */
    fun recordProfitLossSnapshot() {

        val currentAssets =
            _assets.value

        val total =
            currentAssets.sumOf {
                it.currentValue
            }

        if (total <= 0L) {
            _message.value =
                "ارزش سرمایه برای ثبت سود/زیان معتبر نیست."
            return
        }

        /*
         * ارزش هر عنوان دارایی
         *
         * کلید فعلاً نام دارایی است تا با ساختار
         * categoryValues فعلی سازگار بماند.
         */
        val categoryValues =
            currentAssets
                .groupBy { it.name }
                .mapValues { (_, list) ->
                    list.sumOf {
                        it.currentValue
                    }
                }

        val point =
            HistoryPoint(
                timestamp = System.currentTimeMillis(),
                totalToman = total,
                categoryValues = categoryValues
            )

        /*
         * فقط Snapshotهای ثبت‌شده توسط کاربر ذخیره می‌شوند.
         *
         * حداکثر 365 نقطه نگه می‌داریم.
         */
        val updatedHistory =
            (
                _history.value + point
            ).takeLast(365)

        _history.value =
            updatedHistory

        repo.saveHistory(
            updatedHistory
        )

        /*
         * پیام مناسب برای کاربر
         */
        if (updatedHistory.size == 1) {

            _message.value =
                "نقطه شروع سود/زیان با موفقیت ثبت شد."

        } else {

            val previous =
                updatedHistory[
                    updatedHistory.lastIndex - 1
                ]

            val difference =
                total - previous.totalToman

            val percent =
                if (previous.totalToman > 0L) {
                    difference * 100.0 /
                            previous.totalToman
                } else {
                    0.0
                }

            val sign =
                if (difference >= 0L) "+" else ""

            _message.value =
                "سود/زیان ثبت شد: " +
                        "$sign$difference تومان " +
                        "(${String.format("%.2f", percent)}%)"
        }
    }

    /*
     * پاک کردن پیام وضعیت
     */
    fun clearMessage() {
        _message.value = null
    }

    /*
     * پشتیبان‌گیری
     */
    fun backup(): String =
        repo.backup()

    /*
     * بازیابی اطلاعات
     */
    fun restore(json: String) {

        repo.restore(json)

        _assets.value =
            repo.assets()

        _history.value =
            repo.history()
    }
}
