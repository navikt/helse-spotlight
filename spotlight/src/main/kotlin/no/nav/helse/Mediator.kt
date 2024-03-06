package no.nav.helse

import no.nav.helse.kafka.KommandokjedeFerdigstiltRiver
import no.nav.helse.kafka.KommandokjedeSuspendertRiver
import no.nav.helse.rapids_rivers.RapidsConnection

class Mediator(rapidsConnection: RapidsConnection) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
    }
}