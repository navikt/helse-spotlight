package no.nav.helse

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.db.DataSourceBuilder
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.slack.SlackClient

internal val objectMapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

internal class App {

    private val env = System.getenv()
    private val rapidsConnection = RapidApplication.create(env)
    private val datasourceBuilder = DataSourceBuilder(env)
    private val dataSource = datasourceBuilder.getDataSource()

    private val kommandokjedeDao = KommandokjedeDao(dataSource)
    private val meldingssender = Meldingssender(rapidsConnection)
    private val slackClient = SlackClient(
        accessToken = env.getValue("SLACK_SPY_ACCESS_TOKEN"),
        channel = env.getValue("SLACK_SPOTLIGHT_CHANNEL_ID")
    )

    init {
        Mediator(rapidsConnection, slackClient, meldingssender, kommandokjedeDao)
    }

    internal fun start() {
        datasourceBuilder.migrate()
        rapidsConnection.start()
    }

}

internal fun main() {
    App().start()
}

