package de.tobihxd.autocomment;

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import de.tobihxd.autocomment.settings.PluginSettings
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LmStudioClient(project: Project) {
    val settings = PluginSettings.getInstance(project)
    private val client = OkHttpClient.Builder()
        .connectTimeout(settings.state.connectTimeoutSec.toLong(), TimeUnit.SECONDS)
        .writeTimeout(settings.state.writeTimeoutSec.toLong(), TimeUnit.SECONDS)
        .readTimeout(settings.state.readTimeoutSec.toLong(), TimeUnit.SECONDS)
        .build()
    fun getModels(): List<String> {
        val request = Request.Builder()
            .url(settings.state.serverUrl + "/api/v1/models")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return emptyList()

                println("HTTP ${response.code}")
                println(body)

                if (!response.isSuccessful) return emptyList()

                val modelsArray = JSONObject(body).getJSONArray("models")
                val result = mutableListOf<String>()

                for (i in 0 until modelsArray.length()) {
                    val model = modelsArray.getJSONObject(i)

                    // nur LLMs
                    if (model.optString("type") != "llm") continue

                    // Key verwenden, nicht selected_variant, um unnötiges Nachladen zu vermeiden
                    val id = model.getString("key")
                    result.add(id)
                }

                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    /**
     * Generiert einen kurzen Javadoc-Kommentar für die übergebene Methode.
     * Prüft den ProgressIndicator regelmäßig, um Abbruch zu ermöglichen.
     */
    fun generateJavadocForMethod(methodCode: String,project: Project, indicator: ProgressIndicator? = null): String? {
        val requestJson = JSONObject().apply {
            put("model", settings.state.model)
            put(
                "input",
                Messages.message("prompt.javadoc.generation.json", project) + "\n$methodCode".trimIndent()
            )
            put("reasoning", JSONObject().apply {
                put("effort", settings.state.detailLevel)
            })
        }

        val body = requestJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(settings.state.serverUrl + "/v1/responses")
            .post(body)
            .build()

        val call = client.newCall(request)

        try {
            // Abbruch, falls ProgressIndicator cancelt
            indicator?.let {
                if (it.isCanceled) {

                    call.cancel()
                    return null
                }
            }

            call.execute().use { response ->
                if (!response.isSuccessful) return null

                val responseBody = response.body?.string() ?: return null

                val text = JSONObject(responseBody)
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text")

                return stripMarkdown(text)
            }

        } catch (e: IOException) {
            // SocketException / closed connection, falls abgebrochen
            if (e.message?.contains("Socket closed") == true) return null
            throw e
        }
    }

    private fun stripMarkdown(text: String): String {
        return text
            .trim()
            .removePrefix("```java")
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }
}
