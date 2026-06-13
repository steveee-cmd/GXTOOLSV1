package com.gxtools.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

data class NetworkStatus(
    val connectionType: String,
    val pingMs: Long?, // null kalau gagal/timeout
    val isConnected: Boolean
)

object PingMonitor {

    private val TEST_HOSTS = listOf(
        "1.1.1.1" to 443,   // Cloudflare
        "8.8.8.8" to 443    // Google DNS
    )

    /**
     * Cek tipe koneksi (WiFi/Seluler/Tidak ada) dan ukur ping (round-trip TCP handshake)
     * ke server publik. Dipanggil dari coroutine (IO dispatcher).
     */
    suspend fun check(context: Context): NetworkStatus = withContext(Dispatchers.IO) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }

        val type = when {
            caps == null -> "Tidak ada koneksi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Data Seluler"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Lainnya"
        }

        val isConnected = caps != null &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        val ping = if (isConnected) measurePing() else null

        NetworkStatus(connectionType = type, pingMs = ping, isConnected = isConnected)
    }

    private fun measurePing(): Long? {
        for ((host, port) in TEST_HOSTS) {
            val result = runCatching {
                val start = System.currentTimeMillis()
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), 1500)
                }
                System.currentTimeMillis() - start
            }
            if (result.isSuccess) return result.getOrNull()
        }
        return null
    }
}
