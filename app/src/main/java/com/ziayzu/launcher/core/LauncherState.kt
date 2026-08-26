package com.ziayzu.launcher.core

import android.content.Context
import com.ziayzu.launcher.ZiayzuApp
import org.json.JSONObject

data class McAccount(
    val name: String,
    val uuid: String,
    val accessToken: String,
    val refreshToken: String? = null
)

data class LaunchOptions(
    val versionId: String,
    val renderer: String,
    val memoryMb: Int,
    val javaArgs: String,
    val virtualMouse: Boolean
)

object Renderers {
    val ALL = listOf("GL4ES · GLES 2", "Zink · Vulkan", "ANGLE · GLES 3", "VirGL · Streamed")
    val DEFAULT = ALL.first()
}

object Prefs {
    private val sp by lazy {
        ZiayzuApp.instance.getSharedPreferences("ziayzu_prefs", Context.MODE_PRIVATE)
    }

    var seenOnboarding: Boolean
        get() = sp.getBoolean("onboarding_done", false)
        set(v) = sp.edit().putBoolean("onboarding_done", v).apply()

    var renderer: String
        get() = sp.getString("renderer", Renderers.DEFAULT) ?: Renderers.DEFAULT
        set(v) = sp.edit().putString("renderer", v).apply()

    var memoryMb: Int
        get() = sp.getInt("memory_mb", 4096)
        set(v) = sp.edit().putInt("memory_mb", v).apply()

    var javaArgs: String
        get() = sp.getString("java_args", "") ?: ""
        set(v) = sp.edit().putString("java_args", v).apply()

    var virtualMouse: Boolean
        get() = sp.getBoolean("virtual_mouse", true)
        set(v) = sp.edit().putBoolean("virtual_mouse", v).apply()

    fun saveAccount(a: McAccount) {
        sp.edit().putString("account", JSONObject().apply {
            put("name", a.name)
            put("uuid", a.uuid)
            put("token", a.accessToken)
            a.refreshToken?.let { put("refresh", it) }
        }.toString()).apply()
    }

    fun loadAccount(): McAccount? {
        val raw = sp.getString("account", null) ?: return null
        return runCatching {
            val o = JSONObject(raw)
            McAccount(
                name = o.getString("name"),
                uuid = o.getString("uuid"),
                accessToken = o.getString("token"),
                refreshToken = o.optString("refresh").ifBlank { null }
            )
        }.getOrNull()
    }
}

object Session {
    var account: McAccount? = Prefs.loadAccount()
    var selectedVersion: String = "1.21.4"

    fun options() = LaunchOptions(
        versionId = selectedVersion,
        renderer = Prefs.renderer,
        memoryMb = Prefs.memoryMb,
        javaArgs = Prefs.javaArgs,
        virtualMouse = Prefs.virtualMouse
    )
}
