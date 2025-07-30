package br.edu.utfpr.menfin.data.service

import br.edu.utfpr.menfin.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part> = emptyList()
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val index: Int? = null,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)

@Serializable
data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null
)

class GeminiService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getGenerativeContent(prompt: String): Result<String> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val requestBody = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            )
        )

        return try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val geminiResponse = response.body<GeminiResponse>()
                val responseText =
                    geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    Result.success(responseText)
                } else {
                    Result.failure(Exception("Resposta da API vazia ou em formato inesperado."))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.status.value} - ${response.body<String>()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
