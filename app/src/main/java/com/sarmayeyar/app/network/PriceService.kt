package com.sarmayeyar.app.network

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

data class LivePrices(
    val usdtToman: Long? = null,
    val gold18TomanPerGram: Long? = null,
    val fetchedAt: Long = System.currentTimeMillis()
)

class PriceService {

    fun fetch(): LivePrices {
        val usdt = fetchUsdtFromNobitex()
        val gold = fetchGold18FromTgju()

        return LivePrices(
            usdtToman = usdt,
            gold18TomanPerGram = gold
        )
    }

    private fun fetchUsdtFromNobitex(): Long? {
        return runCatching {

            val url = URL(
                "https://apiv2.nobitex.ir/market/stats"
            )

            val conn = url.openConnection() as HttpURLConnection

            try {
                conn.requestMethod = "POST"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.doOutput = true

                conn.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                )

                conn.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0"
                )

                conn.outputStream.use {
                    it.write(
                        """{"srcCurrency":"usdt","dstCurrency":"rls"}"""
                            .toByteArray(Charsets.UTF_8)
                    )
                }

                if (conn.responseCode !in 200..299) {
                    return@runCatching null
                }

                val text = conn.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val latest = JSONObject(text)
                    .optJSONObject("stats")
                    ?.optJSONObject("usdt-rls")
                    ?.optString("latest")
                    ?.replace(",", "")
                    ?.toDoubleOrNull()
                    ?: return@runCatching null

                if (latest <= 0) {
                    return@runCatching null
                }

                (latest / 10.0).toLong()

            } finally {
                conn.disconnect()
            }

        }.getOrNull()
    }

    private fun fetchGold18FromTgju(): Long? {
        return runCatching {

            val url = URL(
                "https://www.tgju.org/profile/geram18"
            )

            val conn = url.openConnection() as HttpURLConnection

            try {
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/131.0 Safari/537.36"
                )

                conn.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml"
                )

                conn.setRequestProperty(
                    "Accept-Language",
                    "fa-IR,fa;q=0.9,en-US;q=0.8,en;q=0.7"
                )

                if (conn.responseCode !in 200..299) {
                    return@runCatching null
                }

                val html = conn.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                /*
                 * ابتدا الگوهای مربوط به ساختار صفحه را بررسی می‌کنیم.
                 */
                val patterns = listOf(

                    Pattern.compile(
                        """data-field="price"[^>]*>\s*([0-9,٬]+)"""
                    ),

                    Pattern.compile(
                        """data-field=['"]price['"][^>]*>.*?([0-9,٬]{7,})"""
                    ),

                    Pattern.compile(
                        """class="[^"]*price[^"]*"[^>]*>.*?([0-9,٬]{7,})"""
                    ),

                    Pattern.compile(
                        """نرخ فعلی[^0-9]{0,150}([0-9,٬]{7,})"""
                    ),

                    Pattern.compile(
                        """قیمت[^0-9]{0,100}([0-9,٬]{7,})"""
                    )
                )

                var rial: Long? = null

                for (pattern in patterns) {
                    val matcher = pattern.matcher(html)

                    if (matcher.find()) {
                        val value = matcher.group(1)
                            ?.replace(",", "")
                            ?.replace("٬", "")
                            ?.trim()
                            ?.toLongOrNull()

                        if (value != null && value > 0) {
                            rial = value
                            break
                        }
                    }
                }

                /*
                 * اگر الگوهای بالا جواب ندادند،
                 * تمام اعداد بزرگ موجود در HTML بررسی می‌شوند.
                 */
                if (rial == null) {

                    val numberPattern = Pattern.compile(
                        """(?<!\d)([0-9]{7,12})(?!\d)"""
                    )

                    val matcher =
                        numberPattern.matcher(html)

                    while (matcher.find()) {

                        val value =
                            matcher.group(1)
                                ?.toLongOrNull()

                        /*
                         * قیمت طلای ۱۸ باید در محدوده
                         * منطقی قیمت هر گرم باشد.
                         */
                        if (
                            value != null &&
                            value in 10_000_000L..2_000_000_000L
                        ) {
                            rial = value
                            break
                        }
                    }
                }

                val finalRial =
                    rial ?: return@runCatching null

                /*
                 * TGJU قیمت را به ریال ارائه می‌کند.
                 * سرمایه‌یار تومان نگهداری می‌کند.
                 */
                finalRial / 10L

            } finally {
                conn.disconnect()
            }

        }.getOrNull()
    }
}
