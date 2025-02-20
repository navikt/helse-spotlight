package no.nav.helse

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.db.DataSourceBuilder
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.slack.SlackClient

internal val objectMapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

internal class App: RapidsConnection.StatusListener {

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
        rapidsConnection.register(this)
        Mediator(rapidsConnection, slackClient, meldingssender, kommandokjedeDao)
    }

    internal fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        datasourceBuilder.migrate()
    }

}

internal fun main() {
    App().start()
}

