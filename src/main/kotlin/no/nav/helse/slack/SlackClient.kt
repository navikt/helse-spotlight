package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMeldingBuilder.byggDagligSlackMelding
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class SlackClient(private val accessToken: String, private val channel: String) {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    }

    internal fun fortellOmKommandokjeder(kommandokjeder: List<KommandokjedeFraDatabase>) {
        if (kommandokjeder.isEmpty()) {
            postMelding(text = ":spotlight: Ingen kommandokjeder sitter fast :spotlight:", ingenKommandokjederSitterFast = true)
        } else {
            // Slack APIet støtter bare 50 blocks pr melding. Hvis det er mer enn 50 stuck kommandokjeder
            // postes resterende i tråd.
            var threadTs: String? = null
            kommandokjeder.chunked(49).forEach {
                if (threadTs == null) {
                    threadTs = postMelding(attachments = it.byggDagligSlackMelding(kommandokjeder.size))
                } else {
                    postMelding(attachments = it.byggDagligSlackMelding(), threadTs = threadTs)
                }
            }
        }
    }

    private fun postMelding(text: String? = null, attachments: String? = null, threadTs: String? = null, ingenKommandokjederSitterFast: Boolean = false): String? =
        "https://slack.com/api/chat.postMessage".post(
            objectMapper.writeValueAsString(mutableMapOf<String, Any>(
                "channel" to channel,
            ).apply {
                text?.also { put("text", it) }
                attachments?.also { put("attachments", it) }
                threadTs?.also { put("thread_ts", it) }
            }), ingenKommandokjederSitterFast
        )?.let {
            objectMapper.readTree(it)["ts"]?.asText()
        }

    private fun String.post(jsonPayload: String, ingenKommandokjederSitterFast: Boolean): String? {
        val request = HttpRequest.newBuilder()
            .uri(URI(this))
            .timeout(Duration.ofSeconds(50))
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Authorization", "Bearer ${accessToken}")
            .header("User-Agent", "navikt/spotlight")
            .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build()
        val client = HttpClient.newHttpClient()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            val statusCode = response.statusCode()
            if (statusCode !in 200..299) {
                logg.warn("Respons fra slack: code=$statusCode")
                sikkerlogg.warn("Respons fra slack: code=$statusCode body=${response.body()}")
                return null
            }

            val responseBody = response.body()
            logg.debug("Respons fra slack: code=$statusCode")
            sikkerlogg.debug("Respons fra slack: code=$statusCode body=$responseBody")

            return responseBody
        } catch (err: SocketTimeoutException) {
            if (ingenKommandokjederSitterFast) {
                logg.warn("Forsøkte å poste gladmelding til slack om at ingen kommandokjeder sitter fast - fikk timeout", err)
            }
            else {
                logg.error("Timeout venter på svar", err)
                sikkerlogg.error("Timeout venter på svar, forsøker å poste melding: $jsonPayload", err)
            }
        } catch (err: IOException) {
            if (ingenKommandokjederSitterFast) {
                logg.warn("Forsøkte å poste gladmelding til slack om at ingen kommandokjeder sitter fast - fikk feil: {}", err.message, err)
            } else {
                logg.error("Feil ved posting til slack: {}", err.message, err)
                sikkerlogg.error("Feil ved posting til slack med melding: $jsonPayload Error: {}", err.message, err)
            }
        } finally {
            client.close()
        }

        return null
    }
}
