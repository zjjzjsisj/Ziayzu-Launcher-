package com.ziayzu.launcher.core.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object VersionManifest {

    private const val MANIFEST_URL =
        "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"

    data class McVersion(
        val id: String,
        val type: String,
        val url: String,
        val releaseTime: String
    )

    /** Fetches Mojang's official, public version manifest. */
    suspend fun fetch(): List<McVersion> = withContext(Dispatchers.IO) {
        val root = JSONObject(Http.get(MANIFEST_URL))
        val arr = root.getJSONArray("versions")
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    McVersion(
                        id = o.getString("id"),
                        type = o.getString("type"),
                        url = o.getString("url"),
                        releaseTime = o.getString("releaseTime")
                    )
                )
            }
        }
    }
}
