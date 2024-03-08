package no.nav.helse.slack

import no.nav.helse.objectMapper
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI

class SlackClient(private val accessToken: String, private val channel: String) {

    private companion object {
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
        private val logg = LoggerFactory.getLogger(SlackClient::class.java)
    }

    fun postMessage(text: String? = null, attachments: String? = null): String? =
        "https://slack.com/api/chat.postMessage".post(objectMapper.writeValueAsString(mutableMapOf<String, Any>(
            "channel" to channel,
        ).also {
            if (text != null)
                it["text"] = text
            if (attachments != null)
                it["attachments"] = attachments
        }))

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
                logg.warn("response from slack: code=$responseCode")
                sikkerlogg.warn("response from slack: code=$responseCode body=${connection.errorStream.readText()}")
                return null
            }

            val responseBody = connection.inputStream.readText()
            logg.debug("response from slack: code=$responseCode")
            sikkerlogg.debug("response from slack: code=$responseCode body=$responseBody")

            return responseBody
        } catch (err: SocketTimeoutException) {
            logg.warn("timeout waiting for reply", err)
        } catch (err: IOException) {
            logg.error("feil ved posting til slack: {}", err.message, err)
        } finally {
            connection?.disconnect()
        }

        return null
    }

    private fun InputStream.readText() = use { it.bufferedReader().readText() }
}