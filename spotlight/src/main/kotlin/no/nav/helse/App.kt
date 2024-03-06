package no.nav.helse

import no.nav.helse.rapids_rivers.RapidApplication

class App {
    private val rapidsConnection = RapidApplication.create(System.getenv())

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection)
        KommandokjedeSuspendertRiver(rapidsConnection)
    }
    internal fun start() = rapidsConnection.start()

}

internal fun main() {
    App().start()
}

