package no.nav.helse

import com.zaxxer.hikari.HikariDataSource
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.kafka.KommandokjedeFerdigstiltRiver
import no.nav.helse.kafka.KommandokjedeSuspendertRiver
import no.nav.helse.rapids_rivers.RapidsConnection

class Mediator(
    rapidsConnection: RapidsConnection,
    dataSource: HikariDataSource,
    private val kommandokjedeDao: KommandokjedeDao = KommandokjedeDao(dataSource)
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeFerdigstilt() {
        TODO("kall passende dao-metode")
    }

    internal fun kommandokjedeSuspendert() {
        TODO("kall passende dao-metode")
    }

}