package no.nav.helse

import no.nav.helse.db.DataSourceBuilder
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.slack.SlackClient

class App {
    private val env = System.getenv()
    private val rapidsConnection = RapidApplication.create(env)
    private val datasourceBuilder = DataSourceBuilder(env)
    private val dataSource = datasourceBuilder.getDataSource()

    private val slackClient = SlackClient(
            accessToken = env.getValue("SLACK_SPY_ACCESS_TOKEN"),
            channel = env.getValue("SLACK_SPOTLIGHT_CHANNEL_ID")
        )

    init {
        Mediator(rapidsConnection = rapidsConnection, dataSource = dataSource, slackClient = slackClient)
    }
    internal fun start() {
        datasourceBuilder.migrate()
        rapidsConnection.start()
    }

}

internal fun main() {
    App().start()
}

