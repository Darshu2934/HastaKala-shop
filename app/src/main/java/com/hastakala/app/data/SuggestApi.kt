package com.hastakala.app.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

data class SuggestSuccess(
    val productDescription: String,
    val seoTitle: String,
    val seoMetaDescription: String,
    val seoTags: List<String>,
)

private data class SuggestDto(
    val productDescription: String?,
    val seoTitle: String?,
    @SerializedName("seoMetaDescription")
    val seoMetaDescription: String?,
    val seoTags: List<String>?,
)

private data class SuggestErrorBody(val error: String?)

private val gson = Gson()
private val jsonMedia = "application/json; charset=utf-8".toMediaType()

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(90, TimeUnit.SECONDS)
    .build()

/** Base URL only: `http://10.0.2.2:3000` or `http://192.168.1.5:3000` — not the full `/api/suggest` path. */
internal fun normalizeSuggestBaseUrl(raw: String): String {
    var s = raw.trim().trimEnd('/')
    if (s.endsWith("/api/suggest", ignoreCase = true)) {
        s = s.removeSuffix("/api/suggest").trimEnd('/')
    }
    return s
}

private fun Throwable.toSuggestFailureMessage(): String = when (this) {
    is UnknownHostException ->
        "Cannot reach server host. On a real phone, use your PC's LAN IP in local.properties (suggest.api.base.url), not localhost."
    is SocketTimeoutException ->
        "Connection timed out. Start the web server (cd web && npm run dev) and check firewall allows port 3000."
    is ConnectException ->
        "Connection refused. Is \"npm run dev\" running in the web/ folder? Emulator: use http://10.0.2.2:3000"
    is IOException ->
        "Network error: ${message ?: javaClass.simpleName}"
    is JsonSyntaxException ->
        "Bad JSON from server. Make sure the base URL points to this project's Next.js app (port 3000), not another site."
    else -> message ?: "Request failed (${javaClass.simpleName})"
}

suspend fun requestProductSuggestions(
    baseUrl: String,
    title: String,
    material: String,
): Result<SuggestSuccess> = withContext(Dispatchers.IO) {
    val root = normalizeSuggestBaseUrl(baseUrl)
    if (root.isEmpty()) {
        return@withContext Result.failure(IllegalStateException("empty_base"))
    }
    val url = "$root/api/suggest"
    val payload = gson.toJson(mapOf("title" to title, "material" to material))
    val request = Request.Builder()
        .url(url)
        .post(payload.toRequestBody(jsonMedia))
        .header("Accept", "application/json")
        .build()

    try {
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val serverMsg = runCatching {
                    gson.fromJson(body, SuggestErrorBody::class.java).error
                }.getOrNull()
                val hint = serverMsg?.takeIf { it.isNotBlank() }
                    ?: "HTTP ${response.code}. Body: ${body.take(200).trim()}"
                return@withContext Result.failure(IllegalStateException(hint))
            }
            val parsed = try {
                gson.fromJson(body, SuggestDto::class.java)
            } catch (e: JsonSyntaxException) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Could not parse JSON. ${e.message}. First 200 chars: ${body.take(200)}",
                        e,
                    ),
                )
            }
            val desc = parsed.productDescription
            val st = parsed.seoTitle
            val meta = parsed.seoMetaDescription
            val tags = parsed.seoTags
            if (desc.isNullOrBlank() || st.isNullOrBlank() || meta.isNullOrBlank() || tags == null) {
                return@withContext Result.failure(
                    IllegalStateException("Server JSON missing fields. Got keys in response? Check OpenAI /api/suggest."),
                )
            }
            Result.success(
                SuggestSuccess(
                    productDescription = desc,
                    seoTitle = st,
                    seoMetaDescription = meta,
                    seoTags = tags,
                ),
            )
        }
    } catch (e: Exception) {
        Result.failure(IllegalStateException(e.toSuggestFailureMessage(), e))
    }
}
