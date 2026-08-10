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
        return LivePrices(usdt, gold)
    }

    private fun fetchUsdtFromNobitex(): Long? = runCatching {
        val conn = URL("https://api.nobitex.ir/market/stats").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.outputStream.use { it.write("""{"srcCurrency":"usdt","dstCurrency":"rls"}""".toByteArray()) }
        val text = conn.inputStream.bufferedReader().use { it.readText() }
        val value = JSONObject(text).getJSONObject("stats")
            .getJSONObject("usdt-rls").getString("latest").toDouble()
        (value / 10.0).toLong() // ریال به تومان
    }.getOrNull()

    private fun fetchGold18FromTgju(): Long? = runCatching {
        val conn = URL("https://www.tgju.org/profile/geram18").openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.setRequestProperty("User-Agent", "SarmayeYar/1.0")
        val html = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

        val patterns = listOf(
            Pattern.compile("""نرخ فعلی[^0-9]{0,80}([0-9,]{6,})"""),
            Pattern.compile("""current[^0-9]{0,80}([0-9,]{6,})""")
        )
        val rial = patterns.asSequence()
            .map { it.matcher(html) }
            .firstNotNullOfOrNull { m -> if (m.find()) m.group(1) else null }
            ?.replace(",", "")
            ?.toLongOrNull()
            ?: return@runCatching null

        rial / 10L // ریال به تومان
    }.getOrNull()
}
