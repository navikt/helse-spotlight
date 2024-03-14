package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMeldingBuilder.byggSlackMelding
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI

internal class SlackClient(private val accessToken: String, private val channel: String) {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    }

    internal fun fortellOmSuspenderteKommandokjeder(suspenderteKommandokjeder: List<KommandokjedeSuspendertFraDatabase>) {
        if (suspenderteKommandokjeder.isEmpty()) {
            postMelding(text = ":spotlight: Ingen kommandokjeder sitter fast :spotlight:")
        } else {
            // Slack APIet støtter bare 50 blocks pr melding. Hvis det er mer enn 50 stuck kommandokjeder
            // postes resterende i tråd.
            var threadTs: String? = null
            suspenderteKommandokjeder.chunked(49).forEach {
                if (threadTs == null) {
                    threadTs = postMelding(attachments = it.byggSlackMelding(suspenderteKommandokjeder.size))
                } else {
                    postMelding(attachments = it.byggSlackMelding(), threadTs = threadTs)
                }
            }
        }
    }

    private fun postMelding(text: String? = null, attachments: String? = null, threadTs: String? = null): String? =
        "https://slack.com/api/chat.postMessage".post(
            objectMapper.writeValueAsString(mutableMapOf<String, Any>(
                "channel" to channel,
            ).apply {
                text?.also { put("text", it) }
                attachments?.also { put("attachments", it) }
                threadTs?.also { put("thread_ts", it) }
            })
        )?.let {
            objectMapper.readTree(it)["ts"]?.asText()
        }

    private fun String.post(jsonPayload: String): String? {
        var connection: HttpURLConnection? = null
        try {
            connection = (URI(this).toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("User-Agent", "navikt/spotlight")

                outputStream.use { it.bufferedWriter(Charsets.UTF_8).apply { write(jsonPayload); flush() } }
            }

            val responseCode = connection.responseCode

            if (connection.responseCode !in 200..299) {
                logg.warn("Respons fra slack: code=$responseCode")
                sikkerlogg.warn("Respons fra slack: code=$responseCode body=${connection.errorStream.readText()}")
                return null
            }

            val responseBody = connection.inputStream.readText()
            logg.debug("Respons fra slack: code=$responseCode")
            sikkerlogg.debug("Respons fra slack: code=$responseCode body=$responseBody")

            return responseBody
        } catch (err: SocketTimeoutException) {
            logg.warn("Timeout venter på svar", err)
        } catch (err: IOException) {
            logg.error("Feil ved posting til slack: {}", err.message, err)
        } finally {
            connection?.disconnect()
        }

        return null
    }

    private fun InputStream.readText() = use { it.bufferedReader().readText() }

}