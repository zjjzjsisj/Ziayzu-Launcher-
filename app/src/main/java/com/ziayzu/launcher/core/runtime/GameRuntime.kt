package com.ziayzu.launcher.core.runtime

import com.ziayzu.launcher.core.LaunchOptions
import com.ziayzu.launcher.core.McAccount
import java.io.File

/**
 * ─────────────────────────────────────────────────────────────
 *  PHASE 2 PLUG-IN POINT
 * ─────────────────────────────────────────────────────────────
 *  This is where the engine that actually EXECUTES the game connects.
 *  Running Java Edition on Android needs three big pieces:
 *
 *  1. A JVM compiled for Android aarch64 (runs the game's classes)
 *  2. An OpenGL → GLES/Vulkan translation layer (phones don't have desktop GL)
 *  3. An LWJGL backend that feeds touch input into the game
 *
 *  Implement this interface, call RuntimeRegistry.register(...) in
 *  ZiayzuApp.onCreate(), and the whole UI + Play screen light up.
 *
 *  prepare() should: download remaining libraries + assets, swap in
 *  Android-compatible natives, and validate everything.
 *  launch() should: build the classpath, start the JVM with the
 *  user's session token, and hand control to the game.
 */
interface GameRuntime {
    val id: String
    val label: String
    val description: String

    fun isAvailable(): Boolean

    suspend fun prepare(
        gameDir: File,
        options: LaunchOptions,
        onProgress: (Float, String) -> Unit
    )

    fun launch(account: McAccount, options: LaunchOptions)
}

object RuntimeRegistry {
    private val runtimes = mutableListOf<GameRuntime>()

    fun register(runtime: GameRuntime) { runtimes += runtime }
    fun all(): List<GameRuntime> = runtimes.toList()
    fun active(): GameRuntime? = runtimes.firstOrNull { it.isAvailable() }
}
