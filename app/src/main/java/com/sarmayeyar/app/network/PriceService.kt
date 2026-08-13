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

                val requestBody =
                    """{"srcCurrency":"usdt","dstCurrency":"rls"}"""

                conn.outputStream.use { output ->
                    output.write(
                        requestBody.toByteArray(Charsets.UTF_8)
                    )
                    output.flush()
                }

                val responseCode = conn.responseCode

                if (responseCode !in 200..299) {
                    return@runCatching null
                }

                val response = conn.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val root = JSONObject(response)

                val stats = root.optJSONObject("stats")
                    ?: return@runCatching null

                val market = stats.optJSONObject("usdt-rls")
                    ?: return@runCatching null

                val latestText =
                    market.optString("latest", "")

                val latest = latestText
                    .replace(",", "")
                    .toDoubleOrNull()
                    ?: return@runCatching null

                if (latest <= 0) {
                    return@runCatching null
                }

                /*
                 * نوبیتکس قیمت را به ریال می‌دهد.
                 * سرمایه‌یار قیمت را به تومان نگهداری می‌کند.
                 */
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
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
                )

                conn.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml"
                )

                conn.setRequestProperty(
                    "Accept-Language",
                    "fa-IR,fa;q=0.9,en;q=0.8"
                )

                val responseCode = conn.responseCode

                if (responseCode !in 200..299) {
                    return@runCatching null
                }

                val html = conn.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                /*
                 * الگوهای مختلف برای مقاومت بیشتر
                 * در برابر تغییر جزئی HTML سایت.
                 */
                val patterns = listOf(

                    Pattern.compile(
                        """نرخ فعلی[^0-9]{0,150}([0-9,]{6,})"""
                    ),

                    Pattern.compile(
                        """قیمت[^0-9]{0,100}([0-9,]{6,})"""
                    ),

                    Pattern.compile(
                        """"last_price"[^0-9]{0,50}([0-9,]{6,})"""
                    ),

                    Pattern.compile(
                        """"price"[^0-9]{0,50}([0-9,]{6,})"""
                    )
                )

                val rialText =
                    patterns.asSequence()
                        .map { pattern ->
                            pattern.matcher(html)
                        }
                        .firstNotNullOfOrNull { matcher ->
                            if (matcher.find()) {
                                matcher.group(1)
                            } else {
                                null
                            }
                        }
                        ?: return@runCatching null

                val rial = rialText
                    .replace(",", "")
                    .replace("٬", "")
                    .trim()
                    .toLongOrNull()
                    ?: return@runCatching null

                if (rial <= 0) {
                    return@runCatching null
                }

                /*
                 * TGJU قیمت را به ریال اعلام می‌کند.
                 * تبدیل ریال به تومان.
                 */
                rial / 10L

            } finally {
                conn.disconnect()
            }

        }.getOrNull()
    }
}
