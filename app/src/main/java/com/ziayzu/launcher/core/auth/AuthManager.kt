package com.ziayzu.launcher.core.auth

import com.ziayzu.launcher.core.McAccount
import com.ziayzu.launcher.core.net.Http
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Official Microsoft "device code" login flow → Xbox Live → XSTS → Minecraft services.
 * The user's password is never entered inside the launcher.
 */
object AuthManager {

    // Register a free app at portal.azure.com → "App registrations",
    // enable "Allow public client flows", then paste the Application (client) ID here.
    private const val CLIENT_ID = "PASTE_YOUR_AZURE_CLIENT_ID"

    private const val DEVICE_ENDPOINT =
        "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode"
    private const val TOKEN_ENDPOINT =
        "https://login.microsoftonline.com/consumers/oauth2/v2.0/token"
    private const val SCOPE = "XboxLive.signin offline_access"

    data class DeviceLogin(
        val userCode: String,
        val verifyUrl: String,
        val deviceCode: String,
        val intervalSec: Int,
        val expiresSec: Int
    )

    fun isConfigured(): Boolean = !CLIENT_ID.startsWith("PASTE")

    suspend fun beginDeviceFlow(): DeviceLogin = withContext(Dispatchers.IO) {
        val body = "client_id=$CLIENT_ID&scope=${URLEncoder.encode(SCOPE, "UTF-8")}"
        val o = JSONObject(Http.post(DEVICE_ENDPOINT, body, "application/x-www-form-urlencoded"))
        DeviceLogin(
            userCode = o.getString("user_code"),
            verifyUrl = o.getString("verification_uri"),
            deviceCode = o.getString("device_code"),
            intervalSec = o.optInt("interval", 5),
            expiresSec = o.optInt("expires_in", 900)
        )
    }

    suspend fun awaitSession(device: DeviceLogin): McAccount = withContext(Dispatchers.IO) {
        val deadline = System.currentTimeMillis() + device.expiresSec * 1000L
        while (System.currentTimeMillis() < deadline) {
            delay(device.intervalSec * 1000L)
            val body = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Adevice_code" +
                "&client_id=$CLIENT_ID&device_code=${device.deviceCode}"
            val o = JSONObject(Http.post(TOKEN_ENDPOINT, body, "application/x-www-form-urlencoded"))
            if (o.has("access_token")) {
                return@withContext completeXboxChain(o.getString("access_token"))
            }
            when (o.optString("error")) {
                "authorization_pending", "slow_down" -> Unit
                "expired_token" -> error("Code expired — try again.")
                "authorization_declined" -> error("Login was declined.")
                else -> error("Login failed: ${o.optString("error_description", "unknown error")}")
            }
        }
        error("Login timed out.")
    }

    private fun completeXboxChain(msAccessToken: String): McAccount {
        // 1) Xbox Live user token
        val xbl = JSONObject(
            Http.post(
                "https://user.auth.xboxlive.com/user/authenticate",
                """{"Properties":{"AuthMethod":"RPS","SiteName":"user.auth.xboxlive.com","RpsTicket":"d=$msAccessToken"},"RelyingParty":"http://auth.xboxlive.com","TokenType":"JWT"}""",
                "application/json"
            )
        )
        val uhs = xbl.getJSONObject("DisplayClaims")
            .getJSONArray("xui").getJSONObject(0).getString("uhs")

        // 2) XSTS token
        val xsts = JSONObject(
            Http.post(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                """{"Properties":{"SandboxId":"RETAIL","UserTokens":["${xbl.getString("Token")}"]},"RelyingParty":"rp://api.minecraftservices.com/","TokenType":"JWT"}""",
                "application/json"
            )
        )
        val xErr = xsts.optLong("XErr", 0L)
        if (xErr != 0L) error("Xbox auth error $xErr (account restrictions may apply).")

        // 3) Exchange for a Minecraft services token
        val mc = JSONObject(
            Http.post(
                "https://api.minecraftservices.com/authentication/login_with_xbox",
                """{"identityToken":"XBL3.0 x=$uhs;${xsts.getString("Token")}"}""",
                "application/json"
            )
        )
        val mcToken = mc.getString("access_token")

        // 4) Fetch profile — this also verifies the account owns Java Edition
        val profile = JSONObject(
            Http.get("https://api.minecraftservices.com/minecraft/profile", auth = "Bearer $mcToken")
        )
        return McAccount(
            name = profile.getString("name"),
            uuid = profile.getString("id"),
            accessToken = mcToken
        )
    }
}
