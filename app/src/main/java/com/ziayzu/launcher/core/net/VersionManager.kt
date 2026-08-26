package com.ziayzu.launcher.core.net

import com.ziayzu.launcher.ZiayzuApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object VersionManager {

    data class DownloadState(
        val active: Boolean = false,
        val versionId: String? = null,
        val progress: Float = 0f,
        val label: String = ""
    )

    private val _state = MutableStateFlow(DownloadState())
    val state: StateFlow<DownloadState> = _state.asStateFlow()

    fun gamesDir(): File = File(ZiayzuApp.instance.filesDir, "games").apply { mkdirs() }
    fun versionDir(id: String): File = File(gamesDir(), id)
    fun isInstalled(id: String): Boolean = File(versionDir(id), "client.jar").exists()

    /**
     * Downloads the version metadata + client jar from Mojang's official CDN.
     * Libraries / assets / Android-native replacement is handled by the
     * GameRuntime module (Phase 2) — see core/runtime/GameRuntime.kt.
     */
    suspend fun install(v: VersionManifest.McVersion) = withContext(Dispatchers.IO) {
        try {
            _state.value = DownloadState(true, v.id, 0f, "Fetching metadata")
            val dir = versionDir(v.id).apply { mkdirs() }

            val json = Http.get(v.url)
            File(dir, "${v.id}.json").writeText(json)

            val client = JSONObject(json).getJSONObject("downloads").getJSONObject("client")
            _state.value = DownloadState(true, v.id, 0f, "Downloading client jar")

            download(client.getString("url"), File(dir, "client.jar")) { p ->
                _state.value = DownloadState(true, v.id, p, "Downloading client ${(p * 100).toInt()}%")
            }

            _state.value = DownloadState(false, v.id, 1f, "${v.id} ready")
        } catch (t: Throwable) {
            _state.value = DownloadState(false, null, 0f, "Failed: ${t.message}")
        }
    }

    private fun download(url: String, dest: File, onProgress: (Float) -> Unit) {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 20000
        conn.readTimeout = 60000
        try {
            val total = conn.contentLengthLong
            conn.inputStream.use { input ->
                dest.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024)
                    var read = 0L
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        out.write(buf, 0, n)
                        read += n
                        if (total > 0) onProgress(read.toFloat() / total)
                    }
                }
            }
        } finally {
            conn.disconnect()
        }
    }
}
