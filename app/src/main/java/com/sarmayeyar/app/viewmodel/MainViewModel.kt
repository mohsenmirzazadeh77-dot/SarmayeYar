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

class MainViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val repo =
        InvestmentRepository(
            LocalStore(app)
        )

    private val priceService =
        PriceService()

    private val _assets =
        MutableStateFlow(
            repo.assets()
        )

    val assets: StateFlow<List<Asset>> =
        _assets.asStateFlow()

    private val _history =
        MutableStateFlow(
            repo.history()
        )

    val history: StateFlow<List<HistoryPoint>> =
        _history.asStateFlow()

    private val _prices =
        MutableStateFlow(
            LivePrices()
        )

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


    // =========================================================
    // دارایی‌ها
    // =========================================================

    fun addAsset(
        asset: Asset
    ) {

        val next =
            _assets.value +
                    asset.copy(
                        id =
                            System.currentTimeMillis()
                    )

        _assets.value = next

        repo.saveAssets(next)

        /*
         * عمداً Snapshot ثبت نمی‌کنیم.
         *
         * سود/زیان فقط با دستور دستی کاربر
         * ثبت خواهد شد.
         */
    }


    fun deleteAsset(
        asset: Asset
    ) {

        val next =
            _assets.value.filterNot {
                it.id == asset.id
            }

        _assets.value = next

        repo.saveAssets(next)

        /*
         * Snapshot خودکار ممنوع است.
         */
    }


    fun updateAsset(
        asset: Asset
    ) {

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

        /*
         * Snapshot خودکار ممنوع است.
         */
    }


    // =========================================================
    // قیمت‌های آنلاین
    // =========================================================

    fun refreshPrices() {

        viewModelScope.launch {

            _busy.value = true

            _message.value =
                "در حال دریافت قیمت تتر و طلا..."

            val result =
                withContext(
                    Dispatchers.IO
                ) {
                    priceService.fetch()
                }

            _prices.value = result

            val usdtOk =
                result.usdtToman != null &&
                        result.usdtToman > 0

            val goldOk =
                result.gold18TomanPerGram != null &&
                        result.gold18TomanPerGram > 0

            _message.value =
                when {

                    usdtOk && goldOk ->
                        "تتر و طلای ۱۸ عیار با موفقیت به‌روزرسانی شدند."

                    usdtOk && !goldOk ->
                        "تتر با موفقیت دریافت شد؛ اما قیمت طلای ۱۸ عیار دریافت نشد."

                    !usdtOk && goldOk ->
                        "قیمت طلا دریافت شد؛ اما تتر دریافت نشد."

                    else ->
                        "دریافت قیمت تتر و طلا ناموفق بود."
                }

            /*
             * قیمت آنلاین فقط وضعیت فعلی دارایی را
             * به‌روزرسانی می‌کند.
             *
             * این عملیات Snapshot سود/زیان ایجاد نمی‌کند.
             */
            applyLivePrices(result)

            _busy.value = false
        }
    }


    private fun applyLivePrices(
        prices: LivePrices
    ) {

        val next =
            _assets.value.map { asset ->

                when (asset.type) {

                    "تتر" -> {

                        prices.usdtToman?.let { price ->

                            asset.copy(
                                currentPriceToman = price,
                                isLivePrice = true
                            )

                        } ?: asset
                    }

                    "طلا" -> {

                        prices.gold18TomanPerGram?.let { price ->

                            asset.copy(
                                currentPriceToman = price,
                                isLivePrice = true
                            )

                        } ?: asset
                    }

                    else ->
                        asset
                }
            }

        _assets.value = next

        repo.saveAssets(next)

        /*
         * مهم:
         * اینجا snapshot() نداریم.
         */
    }


    // =========================================================
    // ثبت دستی سود / زیان
    // =========================================================

    fun recordProfitLossSnapshot() {

        val currentAssets =
            _assets.value

        val total =
            currentAssets.sumOf {
                it.currentValue
            }

        if (total <= 0L) {

            _message.value =
                "ابتدا حداقل یک دارایی با ارزش معتبر ثبت کنید."

            return
        }

        /*
         * ارزش هر عنوان دارایی در لحظه ثبت.
         *
         * این مقادیر داخل Snapshot ذخیره می‌شوند
         * و بعداً با ویرایش دارایی تغییر نمی‌کنند.
         */
        val categoryValues =
            currentAssets
                .groupBy {
                    it.name
                }
                .mapValues { (_, list) ->
                    list.sumOf {
                        it.currentValue
                    }
                }

        val point =
            HistoryPoint(
                timestamp =
                    System.currentTimeMillis(),

                totalToman =
                    total,

                categoryValues =
                    categoryValues
            )

        val updatedHistory =
            (
                _history.value + point
            ).takeLast(365)

        _history.value =
            updatedHistory

        repo.saveHistory(
            updatedHistory
        )

        _message.value =
            "محاسبه سود/زیان با موفقیت ثبت شد."
    }


    // =========================================================
    // پشتیبان‌گیری
    // =========================================================

    fun backup(): String =
        repo.backup()


    // =========================================================
    // بازیابی
    // =========================================================

    fun restore(
        json: String
    ) {

        repo.restore(json)

        _assets.value =
            repo.assets()

        _history.value =
            repo.history()
    }


    // =========================================================
    // پیام
    // =========================================================

    fun clearMessage() {
        _message.value = null
    }
}
