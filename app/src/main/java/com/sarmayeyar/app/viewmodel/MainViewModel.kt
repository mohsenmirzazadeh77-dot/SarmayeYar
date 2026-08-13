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

    fun addAsset(asset: Asset) {

        val next =
            _assets.value +
                    asset.copy(
                        id = System.currentTimeMillis()
                    )

        _assets.value = next

        repo.saveAssets(next)

        snapshot()
    }

    fun deleteAsset(asset: Asset) {

        val next =
            _assets.value.filterNot {
                it.id == asset.id
            }

        _assets.value = next

        repo.saveAssets(next)

        snapshot()
    }

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

        snapshot()
    }

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

            /*
             * تشخیص وضعیت دریافت قیمت‌ها
             */

            val usdtOk =
                result.usdtToman != null &&
                        result.usdtToman > 0

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

            /*
             * قیمت‌های آنلاین روی دارایی‌ها اعمال می‌شوند.
             */

            applyLivePrices(result)

            _busy.value = false
        }
    }

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

        snapshot()
    }

    private fun snapshot() {

        val total =
            _assets.value.sumOf {
                it.currentValue
            }

        if (total <= 0L) {
            return
        }

        val h =
            (
                _history.value +
                        HistoryPoint(
                            System.currentTimeMillis(),
                            total
                        )
                ).takeLast(365)

        _history.value = h

        repo.saveHistory(h)
    }

    fun clearMessage() {
        _message.value = null
    }

    fun backup(): String =
        repo.backup()

    fun restore(json: String) {

        repo.restore(json)

        _assets.value =
            repo.assets()

        _history.value =
            repo.history()
    }
}
