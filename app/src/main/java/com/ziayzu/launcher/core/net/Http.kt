package com.ziayzu.launcher.core.net

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object Http {
    private const val UA = "ZiayzuLauncher/1.0 (Android)"

    fun get(url: String, auth: String? = null): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("User-Agent", UA)
            setRequestProperty("Accept", "application/json")
            auth?.let { setRequestProperty("Authorization", it) }
        }
        try {
            if (conn.responseCode !in 200..299) throw IOException("HTTP ${conn.responseCode}")
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    /** Returns the body for both success and error responses (OAuth flows report errors in the body). */
    fun post(url: String, body: String, contentType: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("User-Agent", UA)
            setRequestProperty("Content-Type", contentType)
            setRequestProperty("Accept", "application/json")
        }
        try {
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            return stream?.bufferedReader()?.use { it.readText() }
                ?: throw IOException("HTTP ${conn.responseCode}")
        } finally {
            conn.disconnect()
        }
    }
}
