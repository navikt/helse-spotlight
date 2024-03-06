package no.nav.helse

import no.nav.helse.db.DataSourceBuilder
import no.nav.helse.rapids_rivers.RapidApplication

class App {
    private val rapidsConnection = RapidApplication.create(System.getenv())
    private val datasourceBuilder = DataSourceBuilder(System.getenv())

    private val dataSource = datasourceBuilder.getDataSource()

    init {
        Mediator(rapidsConnection, dataSource)
    }
    internal fun start() {
        //datasourceBuilder.migrate()
        rapidsConnection.start()
    }

}

internal fun main() {
    App().start()
}

