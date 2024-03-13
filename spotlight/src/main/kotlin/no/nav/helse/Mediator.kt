package no.nav.helse

import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.kafka.river.*
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.slack.SlackClient

class Mediator(
    rapidsConnection: RapidsConnection,
    private val slackClient: SlackClient,
    private val meldingssender: Meldingssender,
    private val kommandokjedeDao: KommandokjedeDao,
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
        KommandokjedeAvbruttRiver(rapidsConnection, this)
        KlokkaSeksHverdagerRiver(rapidsConnection, this)
        HverHalvtimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeFerdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltTilDatabase) {
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstilt)
    }

    internal fun kommandokjedeSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertTilDatabase) {
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendert)
    }

    internal fun kommandokjedeAvbrutt(kommandokjedeAvbrutt: KommandokjedeAvbruttTilDatabase) {
        kommandokjedeDao.avbrutt(kommandokjedeAvbrutt)
    }

    internal fun fortellOmSuspenderteKommandokjeder() {
        slackClient.fortellOmSuspenderteKommandokjeder(kommandokjedeDao.hentSuspenderteKommandokjeder())
    }

    internal fun påminnSuspenderteKommandokjeder() {
        meldingssender.påminnSuspenderteKommandokjeder(kommandokjedeDao.hentSuspenderteKommandokjeder())
    }
}